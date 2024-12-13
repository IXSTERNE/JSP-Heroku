import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jodd.util.BCrypt;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        try (Connection conn = Database.getConnection();
             PreparedStatement pst = conn.prepareStatement("SELECT password FROM users WHERE username = ?")) {
             
            pst.setString(1, username);
            
            try (ResultSet rs = pst.executeQuery()) {

                String hashedPasword = Database.getHashedPassword(username);

                if (rs.next()) {

                    if (hashedPasword != null && BCrypt.checkpw(password, hashedPasword)) {

                        String token = JWTUtil.generateToken(username);
                        boolean isValid = JWTUtil.validateToken(token);
                        

                        HttpSession session = request.getSession();
                        session.setAttribute("username", username);
                        session.setAttribute("token", token);
                        session.setAttribute("isValid", isValid);
                        
                        response.sendRedirect("welcome.jsp");
                    } 
                    else {
                        response.sendRedirect("failed.jsp");
                    }
                } else {
                    response.sendRedirect("failed.jsp");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("login.jsp?error=true");
        }
    }
}
