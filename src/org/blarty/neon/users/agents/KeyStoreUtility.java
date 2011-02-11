package org.jini.projects.neon.users.agents;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.x500.X500Principal;
import javax.security.auth.x500.X500PrivateCredential;



import sun.security.pkcs.PKCS9Attribute;
import sun.security.x509.AVA;
import sun.security.x509.CertAndKeyGen;
import sun.security.x509.X500Name;

public class KeyStoreUtility {

        private static final int UNINITIALIZED = 0;

        private static final int INITIALIZED = 1;

        private static final int AUTHENTICATED = 2;

        private static final int LOGGED_IN = 3;

        private static final int PROTECTED_PATH = 0;

        private static final int TOKEN = 1;

        private static final int NORMAL = 2;

        private String filename;

        private KeyStore keyStore;

        private String keyStoreURL;

        private String keyStoreType;

        private char[] keyStorePassword;

        private String keyStoreProvider;

        private boolean debug = true;

        // private javax.security.auth.x500.X500Principal principal;

        // private java.security.cert.CertPath certP = null;
        //
        // private X500PrivateCredential privateCredential;

        private int status = UNINITIALIZED;

        private int validity = 90;

        private String keyAlias = "mykey";

        private boolean verbose = true;

        public static void main(String[] args) throws Exception {
                KeyStoreUtility h = new KeyStoreUtility("jks", "conf/keystore", "keystore".toCharArray());
             //   h.authenticateAlias("bob", "hellothere".toCharArray());
                Map options = new HashMap();
                options.put("name", "John Smith");
                
                options.put("unit", "Catering");
                options.put("org", "Catering Co.");
                options.put("city", "Manchester");
                options.put("state", "Lancashire");
                options.put("country", "UK");
               // options.put("street", "1 Some Street");
                options.put("email", "johnsmith@catering.co.uk");
                options.put("surname", "Smith");
                options.put("givenname","John");
                options.put("initials", "JIS");
                options.put("title", "Mr.");
                try {
                        h.deleteAlias("johns");
                } catch (RuntimeException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
              System.out.println("Deleted alias [johns]");
                if (h.addAlias("johns", "openup".toCharArray(), options)) {
                        h.changePassword("johns", "openup".toCharArray(), "sesame".toCharArray());
                        X500Name dn = h.getAliasDN("johns", "sesame".toCharArray());
                        System.out.println("DN Surname: " + dn.getSurname());
                        System.out.println("DN Given name: " + dn.getGivenName());
                        System.out.println("DN Initials: " + dn.getInitials());
                        System.out.println("DN IP: " + dn.getIP());
                        System.out.println("DN:" + dn.getDNQualifier());
                        System.out.println("DN:" + dn.getLocality());


                } else
                        System.out.println("KeyStore not updated");

        }

        public KeyStoreUtility(String keyStoreType, String filename, char[] keyStorePass) {
                try {
                        this.keyStoreType = keyStoreType;
                        this.filename = filename;
                        keyStoreURL = new File(filename).toURL().toExternalForm();
                        keyStorePassword = keyStorePass;
                        loadKeyStore();

                } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
        }

        private void storeKeyStore() {
                try {
                        OutputStream out = new BufferedOutputStream(new FileOutputStream(filename));
                        keyStore.store(out, keyStorePassword);
                        out.close();
                } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                } catch (CertificateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                } catch (KeyStoreException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
        }

        public boolean addAlias(String alias, char[] password, Map options) throws Exception {
                try {
                        doGenKeyPair(alias, null, "RSA", 1024, null, password, options);
                        storeKeyStore();
                        return true;
                } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                return false;
        }

        private void deleteAlias(String alias) throws Exception {
                if (!keyStore.containsAlias(alias)) {
                        throw new Exception("Alias " + alias + " could not be found in this keystore");
                } else {
                        keyStore.deleteEntry(alias);
                        storeKeyStore();
                        return;
                }
        }

        public boolean addAlias(String alias, char[] password, String dname) throws Exception {
                try {
                        doGenKeyPair(alias, dname, "RSA", 1024, null, password, null);
                        storeKeyStore();
                        return true;
                } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                return false;
        }

        private void doGenKeyPair(String alias, String dname, String keyAlgName, int keysize, String sigAlgName, char[] password, Map options) throws Exception {
                if (alias == null)
                        alias = keyAlias;
                if (keyStore.containsAlias(alias)) {
                        MessageFormat messageformat = new MessageFormat("Key pair not generated, alias <alias> already exists");
                        Object aobj[] = { alias };
                        throw new Exception(messageformat.format(((Object) (aobj))));
                }
                if (sigAlgName == null)
                        if (keyAlgName.equalsIgnoreCase("DSA"))
                                sigAlgName = "SHA1WithDSA";
                        else if (keyAlgName.equalsIgnoreCase("RSA"))
                                sigAlgName = "MD5WithRSA";
                        else
                                throw new Exception("Cannot derive signature algorithm");
                CertAndKeyGen certandkeygen = new CertAndKeyGen(keyAlgName, sigAlgName);
                X500Name x500name;
                if (dname == null)
                        if (options != null)
                                x500name = getX500Name(options);
                        else
                                throw new Exception("Alias can only be created if there is a DN or a map to complete a DN");
                else
                        x500name = new X500Name(dname);
                if (verbose) {
                        MessageFormat messageformat1 = new MessageFormat("Generating keysize bit keyAlgName key pair and self-signed certificate (sigAlgName)\n\tfor: x500Name");
                        Object aobj1[] = { new Integer(keysize), keyAlgName, sigAlgName, x500name };
                        System.err.println(messageformat1.format(((Object) (aobj1))));
                }
                certandkeygen.generate(keysize);
                PrivateKey privatekey = certandkeygen.getPrivateKey();
                X509Certificate ax509certificate[] = new X509Certificate[1];
                ax509certificate[0] = certandkeygen.getSelfCertificate(x500name, validity * 24 * 60 * 60);
                char[] keyPass = password;
                keyStore.setKeyEntry(alias, privatekey, keyPass, ax509certificate);
        }

        
        
        private X500Name getX500Name(Map options) throws IOException {

                X500Name x500name;

//                String name = getNamedStringValue("name", options);
//                String unit = getNamedStringValue("unit", options);
//                String org = getNamedStringValue("org", options);
//                String street = getNamedStringValue("street", options);
//                String city = getNamedStringValue("city", options);
//                String state = getNamedStringValue("state", options);
//                String country = getNamedStringValue("country", options);
//                String email = getNamedStringValue("email", options);
//                String surname = getNamedStringValue("surname", options);
//                String initials = getNamedStringValue("initials", options);
//                String givenname = getNamedStringValue("givenname",options);
//                String serialnumber = getNamedStringValue("serialnumber",options);
//                String title = getNamedStringValue("title",options);
                StringBuffer buffer = new StringBuffer();
                appendX500DnName(buffer, "CN", "name", options);
                appendX500DnName(buffer, "OU", "unit", options);
                appendX500DnName(buffer, "O", "org", options);
                appendX500DnName(buffer, "STREET", "street", options);
                appendX500DnName(buffer, "L", "city", options);
                appendX500DnName(buffer, "ST", "state", options);
                appendX500DnName(buffer, "C", "country", options);
                appendX500DnName(buffer, "T", "title", options);
                appendX500DnName(buffer, "GIVENNAME", "givenname", options);
                appendX500DnName(buffer, "SURNAME", "surname", options);
                appendX500DnName(buffer, "INITIALS", "initials", options);
                appendX500DnName(buffer, "EMAIL", "email", options);
                appendX500DnName(buffer, "SERIALNUMBER", "serialnumber", options);
                appendX500DnName(buffer, "UID", "uid", options);
              
                String dnName  = buffer.toString();
                x500name = new X500Name(dnName.substring(0,dnName.length()-1));
                
                return x500name;
        }
        
        private void appendX500DnName(StringBuffer buffer, String OID, String optname, Map options){
                String value = getNamedStringValue(optname, options);
                //Need to handle items with commas in
                //Escape string is '\,'
                if(!value.equals("")){
                        buffer.append(OID + "=" + value + ",");
                        System.out.println("Appended " + OID + "=> [" + value + "]");
                }
        }

        private String getNamedStringValue(String key, Map options) {
                if (options.containsKey(key)) {
                        return (String) options.get(key);
                } else
                        return "";
        }

        public boolean authenticateAlias(String alias, char[] privatePassword) {

                try {
                        getKeyAndLogin(alias, privatePassword);
                        System.out.println("Authentication showing as GOOD");
                        return true;
                } catch (LoginException e) {
                        // TODO Auto-generated catch block
                        System.out.println("Authentication showing as BAD");
                        e.printStackTrace();
                        return false;
                }

        }

        private void changePassword(String alias, char[] privatePassword, char[] newPassword) throws Exception {
                Object aobj[] = recoverPrivateKey(alias, keyStorePassword, privatePassword);
                PrivateKey privatekey = (PrivateKey) aobj[0];
                if (keyStorePassword == null)
                        keyStorePassword = (char[]) aobj[1];

                keyStore.setKeyEntry(alias, privatekey, newPassword, keyStore.getCertificateChain(alias));
        }

        public X500Name  getAliasDN(String alias, char[] password) throws LoginException {

                /* Get principal and keys */
                try {
                        Certificate[] fromKeyStore = keyStore.getCertificateChain(alias);
                        /* Get certificate chain and create a certificate path */
                        fromKeyStore = getCertificates(alias, fromKeyStore);
                        X509Certificate certificate = (X509Certificate) fromKeyStore[0];
                        X500Principal principal = getPrincipal(certificate);
                        X500Name name =  new X500Name(principal.getName());
                        return name;
                } catch (KeyStoreException e) {
                        LoginException le = new LoginException("Error using keystore");
                        le.initCause(e);
                        throw le;
                } catch (IOException e) {
                        // TODO Auto-generated catch block
                        LoginException le = new LoginException("Error building DN");
                        le.initCause(e);
                        throw le;
                }

        }

        private List getAliases() {
                List list = new ArrayList();
                try {

                        for (Enumeration enumeration = keyStore.aliases(); enumeration.hasMoreElements();) {
                                String s = (String) enumeration.nextElement();
                                list.add(s);
                        }
                } catch (KeyStoreException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                return list;
        }

        public Object[] recoverPrivateKey(String s, char[] ksPassword, char ac1[]) throws Exception {
                java.security.Key key = null;
                if (!keyStore.containsAlias(s)) {
                        throw new Exception("Alias " + s + " does not exist");
                }
                if (!keyStore.isKeyEntry(s)) {

                        throw new Exception("Alias " + s + " has no private key");
                }
                
                key = keyStore.getKey(s, ac1);
                if (!(key instanceof PrivateKey))
                        throw new Exception("Recovered key is not a private key");
                else
                        return (new Object[] { (PrivateKey) key, ac1 });
        }
        
        
        public PublicKey recoverPublicKey(String s) throws Exception {
                java.security.Key key = null;
                if (!keyStore.containsAlias(s)) {
                        throw new Exception("Alias " + s + " does not exist");
                }
                if (!keyStore.isKeyEntry(s)) {

                        throw new Exception("Alias " + s + " has no private key");
                }

                Certificate cert = keyStore.getCertificate(s);
                return cert.getPublicKey();
                
        }

        private X500PrivateCredential getAliasCredential(String keyStoreAlias, char[] privateKeyPassword) throws LoginException {

                /* Get principal and keys */
                try {
                        Certificate[] fromKeyStore = keyStore.getCertificateChain(keyStoreAlias);
                        /* Get certificate chain and create a certificate path */
                        fromKeyStore = getCertificates(keyStoreAlias, fromKeyStore);
                        X509Certificate certificate = (X509Certificate) fromKeyStore[0];
                        X500Principal principal = getPrincipal(certificate);
                        // if token, privateKeyPassword will be null
                        Key privateKey = getKeyAndLogin(keyStoreAlias, privateKeyPassword);

                        return getCredential(keyStoreAlias, certificate, privateKey);
                } catch (KeyStoreException e) {
                        LoginException le = new LoginException("Error using keystore");
                        le.initCause(e);
                        throw le;
                }

        }

        private Key getKeyAndLogin(String keyStoreAlias, char[] privateKeyPassword) throws LoginException {
                Key privateKey;
                try {
                        privateKey = keyStore.getKey(keyStoreAlias, privateKeyPassword);
                } catch (KeyStoreException e) {
                        LoginException le = new LoginException("Error using keystore");
                        le.initCause(e);
                        throw le;
                } catch (NoSuchAlgorithmException e) {
                        LoginException le = new LoginException("Error using keystore");
                        le.initCause(e);
                        throw le;
                } catch (UnrecoverableKeyException e) {
                        FailedLoginException fle = new FailedLoginException("Unable to authenticate alias & key from keystore");
                        fle.initCause(e);
                        throw fle;
                }
                if (privateKey == null || !(privateKey instanceof PrivateKey)) {
                        throw new FailedLoginException("Unable to recover key from keystore");
                }
                return privateKey;
        }

        private X500PrivateCredential getCredential(String keyStoreAlias, X509Certificate certificate, Key privateKey) {
                X500PrivateCredential privateCredential = new X500PrivateCredential(certificate, (PrivateKey) privateKey, keyStoreAlias);
                return privateCredential;
        }

        private X500Principal getPrincipal(X509Certificate certificate) {

                X500Principal principal = new javax.security.auth.x500.X500Principal(certificate.getSubjectDN().getName());
                return principal;
        }

        private Certificate[] getCertificates(String keyStoreAlias, Certificate[] fromKeyStore) throws FailedLoginException, LoginException {
                try {

                        if (fromKeyStore == null || fromKeyStore.length == 0 || !(fromKeyStore[0] instanceof X509Certificate)) {
                                throw new FailedLoginException("Unable to find X.509 certificate chain in keystore");
                        } else {
                                LinkedList certList = new LinkedList();
                                for (int i = 0; i < fromKeyStore.length; i++) {
                                        certList.add(fromKeyStore[i]);
                                }
                                CertificateFactory certF = CertificateFactory.getInstance("X.509");
                                CertPath certP = certF.generateCertPath(certList);
                        }
                } catch (CertificateException ce) {
                        LoginException le = new LoginException("Error: X.509 Certificate type unavailable");
                        le.initCause(ce);
                        throw le;
                }
                return fromKeyStore;
        }

        private void loadKeyStore() throws LoginException {
                /* Get KeyStore instance */
                try {
                        if (keyStoreProvider == null) {
                                keyStore = KeyStore.getInstance(keyStoreType);
                        } else {
                                keyStore = KeyStore.getInstance(keyStoreType, keyStoreProvider);
                        }
                } catch (KeyStoreException e) {
                        LoginException le = new LoginException("The specified keystore type was not available");
                        le.initCause(e);
                        throw le;
                } catch (NoSuchProviderException e) {
                        LoginException le = new LoginException("The specified keystore provider was not available");
                        le.initCause(e);
                        throw le;
                }

                /* Load KeyStore contents from file */
                try {

                        InputStream in = new URL(keyStoreURL).openStream();
                        keyStore.load(in, keyStorePassword);
                        in.close();

                } catch (MalformedURLException e) {
                        LoginException le = new LoginException("Incorrect keyStoreURL option");
                        le.initCause(e);
                        throw le;
                } catch (GeneralSecurityException e) {
                        LoginException le = new LoginException("Error initializing keystore");
                        le.initCause(e);
                        throw le;
                } catch (IOException e) {
                        LoginException le = new LoginException("Error initializing keystore");
                        le.initCause(e);
                        throw le;
                }
        }

        private void debugPrint(String message) {
                // we should switch to logging API
                if (message == null) {
                        System.err.println();
                } else {
                        System.err.println("Debug KeyStoreLoginModule: " + message);
                }
        }

        private void checkAlias() throws LoginException {
        }
}
