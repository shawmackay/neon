/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jini.projects.neon.util.encryption;

/**
 *
 * @author calum
 */
public interface CryptoHolder {
    public void setEncryptedObject(EncryptedHolder encrypted);
    public EncryptedHolder getEncryptedObject();
}
