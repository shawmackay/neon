/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jini.projects.neon.util.encryption;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.*;
import java.security.Key;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Map;
import java.util.TreeMap;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import net.jini.core.entry.Entry;
import net.jini.id.UuidFactory;
import org.jini.projects.neon.agents.AgentState;
import org.jini.projects.neon.agents.util.LocalAgentConstraintsEntry;
import org.jini.projects.neon.users.agents.KeyStoreUtility;
import org.mortbay.util.ByteArrayOutputStream2;

/**
 *
 * @author calum
 */
public class EncryptionTest {

    public static void main(String[] args) throws Exception {
//        System.out.println("Security Providers");
//
//
//
//        System.out.println("Algorithms Supported in Java " + System.getProperty("java.version") + " JCE.");
//        System.out.println("====================");
//        // heading
//        System.out.println("Provider: type.algorithm -> className" + "\n  aliases:" + "\n  attributes:\n");
//
//        // discover providers
//        Provider[] providers = java.security.Security.getProviders();
//        for (Provider provider : providers) {
//            System.out.println("<><><>" + provider + "<><><>\n");
//            // discover services of each provider
//            for (Provider.Service service : provider.getServices()) {
//                System.out.println(service);
//            }
//            System.out.println();
//        }

        KeyStoreUtility ksu = new KeyStoreUtility("jks", "conf/keystore", "keystore".toCharArray());
        Object[] arr = ksu.recoverPrivateKey("johns", "keystore".toCharArray(), "openup".toCharArray());
        PrivateKey privKey = (PrivateKey) arr[0];
        PublicKey pub = ksu.recoverPublicKey("johns");

        LocalAgentConstraintsEntry lace = new LocalAgentConstraintsEntry(null, "org.jini.projects.neon.sercie.ServiceAgentImpl", "file:conf/neon.config", "global", UuidFactory.generate(), null, "Services", "neon", "SomeID", AgentState.AVAILABLE.toString());
        System.out.println("Before Encryption: " + lace);
        
        


        Entry aEntry = lace;
        String name = aEntry.getClass().getName();

        String[] parts = name.split("\\.");
        parts[parts.length - 1] = "Encrypted" + parts[parts.length - 1];
        StringBuffer newName = new StringBuffer();
        for (String part : parts) {
            newName.append(part + ".");
        }
        newName.deleteCharAt(newName.length() - 1);
        String newClassName = new String(newName);
        System.out.println("Looking for class: " + newClassName);
        Class c = null;
        try {
            c = Class.forName(newClassName);
        } catch (Exception e) {
        }
        if (c == null) {
            System.out.println("Encrypted version of object class not found");
        } else {
            System.out.println("Loading Encrypted: ");
            Object o = c.newInstance();
            Map m = new TreeMap();
            Field[] encAttributes = c.getFields();
            Field[] normalAttributes = aEntry.getClass().getFields();
            for (Field normalAttribute : normalAttributes) {
                m.put(normalAttribute.getName(), normalAttribute);
            }
            for (Field encAttrib : encAttributes) {
                if (m.containsKey(encAttrib.getName())) {
                    Object value = ((Field) m.get(encAttrib.getName())).get(aEntry);
                    encAttrib.set(o, value);
                }
            }
            if(o instanceof CryptoHolder){
                CryptoHolder ch = (CryptoHolder) o;
                ch.setEncryptedObject(new EncryptedHolder(EncryptionUtils.encryptObjectWithKey(aEntry, pub)));
            }
            System.out.println("Object: " + o);
        }
        
//        String unecryppted = (String) so.getObject(pub);
//        System.out.println("Credit card number = " + unecryppted);
    }
}
    

