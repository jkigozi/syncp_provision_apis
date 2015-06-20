package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationHelper {
	private static final String PROP_FILE_NAME = "config.properties";
	private static final String OAUTH_AUTORIZATION_URL = "https://api.syncplicity.com/oauth/authorize";
	private static final String OAUTH_TOKEN_URL = "https://api.syncplicity.com/oauth/token";
	private static final String OAUTH_REFRESH_TOKEN_URL = "https://api.syncplicity.com/oauth/token";
	private static final String OAUTH_REVOKE_TOKEN_URL = "https://api.syncplicity.com/oauth/revoke";
	private static final String CONSUMER_REDIRECT_URL = "https://api.syncplicity.com/oauth/callback";
	private static final String BASE_API_ENDPOINT = "https://api.syncplicity.com/";
	private static final String SIMPLE_PASSWORD = "123123aA";
	private static final String REPORTS_FOLDER = "Syncplicity Reports";
	private static final String GROUP_NAME = "SampleAppGroup-";
	private static final String SYNCPOINT_NAME = "SampleAppSyncpoint-";
	private static final String FOLDER_NAME = "SampleAppFolder-";
	private static final String REPORT_NAME ="SampleAppReportStorageByUser-";
	private static Properties settings = null;

	private static Properties getSettings() {
		if (settings == null) {
			settings = new Properties();

			InputStream inputStream = ConfigurationHelper.class.getClassLoader().getResourceAsStream("resources/config.properties");

			if (inputStream != null) {
				try {
					settings.load(inputStream);
				} catch (IOException e) {
					System.err.println("Error reading configuration file: ");
					e.printStackTrace();
				}
			}
		}
		return settings;
	}

	public static String getOAuthAutorizationUrl() {
		return "https://api.syncplicity.com/oauth/authorize";
	}

	public static String getOAuthTokenUrl() {
		return "https://api.syncplicity.com/oauth/token";
	}

	public static String getOAuthRefreshTokenUrl() {
		return "https://api.syncplicity.com/oauth/token";
	}

	public static String getOAuthRevokeTokenUrl() {
		return "https://api.syncplicity.com/oauth/revoke";
	}

	public static String getApplicationKey() {
		return getSettings().getProperty("appKey", "");
	}

	public static String getApplicationSecret() {
		return getSettings().getProperty("appSecret", "");
	}

	public static String getSyncplicityAdminKey() {
		return getSettings().getProperty("syncplicityAdminToken", "");
	}

	public static String getConsumerRedirectUrl() {
		return "https://api.syncplicity.com/oauth/callback";
	}

	public static String getOwnerEmail() {
		return getSettings().getProperty("ownerEmail", "");
	}

	public static String getLdapUrl() {
		return getSettings().getProperty("ldapurl", "");
	}

	public static String getAdminCn() {
		return getSettings().getProperty("admName", "");
	}

	public static String getAdminPass() {
		return getSettings().getProperty("admPass", "");
	}
	
	//pipe-delimited list of all ldap search filters
	public static String all_search_filters() {
		return getSettings().getProperty("all_search_filters", "");
	}

	//pipe-delimited list of all ldap search bases
	public static String all_search_bases() {
		return getSettings().getProperty("all_search_bases", "");
	}
	
	
	public static String getGrp_infil() {
		return getSettings().getProperty("grp_inpfil", "");
	}
	
	public static String getUsr_infil() {
		return getSettings().getProperty("usr_inpfil", "");
	}

	public static String getGroupName() {
		return "SampleAppGroup-";
	}

	public static String getBaseApiEndpointUrl() {
		return "https://api.syncplicity.com/";
		//return "https://xml.syncplicity.com/1.1/";
	}

	public static String getSimplePassword() {
		return "123123aA";
	}
}