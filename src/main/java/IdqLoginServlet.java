
import java.io.IOException;
//import java.io.PrintWriter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

//import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.apache.oltu.oauth2.common.OAuth;
import org.json.JSONException;
import org.json.JSONObject;

@WebServlet(urlPatterns = "/idqlogin")
public class IdqLoginServlet extends HttpServlet{  

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request,HttpServletResponse response) 
    		throws ServletException, IOException {
		
		HttpSession httpSession = request.getSession(true);
		final String authzCode = request.getParameter(ResponseType.CODE.toString());
		
		Properties prop = new Properties();
		//Get file from resources folder
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream input = null;
		input = new FileInputStream(classLoader.getResource("Oauth2.properties").getFile());
		// Load a properties file
		prop.load(input);
		// Obtain OAuth 2 parameters from the properties file
		final String clientID = prop.getProperty("clientID");
		final String clientSecret = prop.getProperty("clientSecret");
		final String redirectUrl = prop.getProperty("redirectUrl");
		final String serverName = prop.getProperty("serverName");
		final String serverPort = prop.getProperty("serverPort");
		final String authzEndpoint = prop.getProperty("authzEndpoint");
		final String tokenEndpoint = prop.getProperty("tokenEndpoint");
		final String userEndpoint = prop.getProperty("userEndpoint");		
		String oauthState;

		// redirectUrl = request.getRequestURL().toString();
		
		IdqClient idqClient = new IdqClient();		
		// Initialize OAuth 2.0 Client Configuration
		idqClient.initOauthClient(clientID, clientSecret, redirectUrl);
		
		// Intialize idQ TaaS Backend Configuration
		idqClient.initOauthServer(serverName, serverPort, authzEndpoint,
			tokenEndpoint, userEndpoint);

		try {

			if (authzCode == null || authzCode.isEmpty()) {
				// Generate the OAuth state using HttpServletRequest request			
				oauthState = httpSession.getId();
				// Build idQ Authentication URL
				String oauthReqUrl = idqClient.getAuthzUrl(oauthState, "email");  
				// HttpServletResponse response redirects user to idQ TaaS backend
				response.sendRedirect(oauthReqUrl);	
				
			} else {
				oauthState = request.getParameter(OAuth.OAUTH_STATE);
				/**
				 * Ensure that the OAuth state corresponds to HttpSession httpSession requesting authorization
				 * Then retrieve the Authz code from HttpServletRequest request
				 * Otherwise, HttpServletResponse response bad authorization request
				 */
				if (!oauthState.equals(httpSession.getId())) {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return;
				}
  
				// Exchange authorization code for access token
				final String accessToken = idqClient.getToken(authzCode);
         
				// Get idQ user info using access token       
				final String userInfo = idqClient.getUserInfo(accessToken);
				JSONObject userObj = new JSONObject(userInfo);
				String username = userObj.getString("username");
				String email = userObj.getString("email");
				// Establish HttpSession httpSession with user ID
				httpSession.setAttribute("name", username);
				String originalUrl = request.getRequestURL().toString();
				String baseUrl = originalUrl.substring(0, originalUrl.length() - request.getRequestURI().length()) + request.getContextPath();
				String target = baseUrl + "/homepage.do";
				response.sendRedirect(response.encodeRedirectURL(target));
			}
			
		} catch(OAuthSystemException | OAuthProblemException | JSONException e) {
			request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
		}
	}

}
