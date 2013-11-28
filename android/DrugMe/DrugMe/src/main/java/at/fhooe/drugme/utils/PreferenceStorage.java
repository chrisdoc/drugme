package at.fhooe.drugme.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
  
/**
 * 
 * @author s1210455002
 *
 */
public class PreferenceStorage {
	Context context;
	SharedPreferences sPreferences ;
	
	/**
	 * 
	 * @param context
	 */
	public PreferenceStorage(Context context){
		this.context = context;	
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public boolean entryExists(String key){
		sPreferences = context.getApplicationContext().getSharedPreferences(key, Context.MODE_PRIVATE);
		return sPreferences.contains(key);
	}
	
	/**
	 * 
	 * @param key
	 * @param value
	 */
	public void putString(String key, String value){
		sPreferences = context.getApplicationContext().getSharedPreferences(key, Context.MODE_PRIVATE);
		Editor prefEditor = sPreferences.edit();
		prefEditor.putString(key, value);
		prefEditor.commit();
	}
	
	/**
	 * 
	 * @param key
	 * @param value
	 */
	public void putBytes(String key, byte[] value){
		sPreferences = context.getApplicationContext().getSharedPreferences(key, Context.MODE_PRIVATE);
		Editor prefEditor = sPreferences.edit();
		prefEditor.putString(key, Converter.base64Encode(value));
		prefEditor.commit();
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public byte[] getBytes(String key){
		sPreferences = context.getApplicationContext().getSharedPreferences(key, Context.MODE_PRIVATE);
		String encoded = sPreferences.getString(key, "");
		return Converter.base64DecodeToBytes(encoded);
	}
	/**
	 * method to save password in the applications private preference storage
	 * plain password is not stored ... the SHA 256 hash of the password is calculated and converted to Hex String before storing
	 * @param password ... password string
	 */
	public void savePassword(String password){	 
		sPreferences = context.getApplicationContext().getSharedPreferences("password", Context.MODE_PRIVATE);
		Editor prefEditor = sPreferences.edit();
		prefEditor.putString("password", Crypto.sha256Hash(password.getBytes()));
		prefEditor.commit();
		
	}
	
	/**
	 * Method to check weather the application is running for the first time or not
	 * based on availability password record in the preference
	 * @return.... return true if application is running for the first time
	 */
	public boolean firstTimeRun(){
		sPreferences = context.getApplicationContext().getSharedPreferences("prk", Context.MODE_PRIVATE);
		if(sPreferences.contains("prk")){
			return false;
		}
		return true;
	}
	
	/**
	 * reads stored password hash from preference and compares with SHA256 hash of incoming password
	 * @param password ... string password
	 * @return  return true if password match otherwise false
	 */
	public boolean checkPassword(String password){
		if(!firstTimeRun()){ 
		   String storedPass = sPreferences.getString("password", "");
		   String passHash = Crypto.sha256Hash(password.getBytes());		   
		   if(storedPass.equals(passHash)){
			   return true;
		   }
		}
		return false;
	}
	
  
}
