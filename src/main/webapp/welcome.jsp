<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Welcome</title>
    <style>
        #tokenExpiredNotice {
            display: none;
            background-color: red;
            color: white;
            padding: 20px;
            text-align: center;
            font-size: 18px;
        }
    </style>
</head>
<body>
    <h1>Welcome, <%= session.getAttribute("username") %>!</h1>
    <a href="private.jsp">Go to another page</a>
    <h1>Your token is = <%= session.getAttribute("token") %></h1>
    <h1>Your token is valid: <%= session.getAttribute("isValid") %></h1>

    <div id="tokenExpiredNotice">Your session has expired. Please log in again.</div>

    <script>
        // Function to decode a JWT token
        function decodeJWT(token) {
            const payload = token.split('.')[1];
            const decoded = JSON.parse(atob(payload));
            return decoded;
        }

        // Function to check if the token is expired
        function isTokenExpired(token) {
            try{
                const decodedToken = decodeJWT(token);
                const expirationTime = decodedToken.exp;  // Expiration time is in seconds, so multiply by 1000 for milliseconds
                const currentTime = Math.floor(Date.now() / 1000);
                return currentTime > expirationTime;
            }
            catch(error){
                console.error('Error decoding token: ', error);
                return true;
            }
        }

        // Example: Get the token from session (you can store it in sessionStorage or localStorage if needed)
        const token = "<%= session.getAttribute("token") %>"; // Get token from the session attribute

        // Check if the token is expired
        if (token && isTokenExpired(token)) {
            // Show the expiration notice
            document.getElementById('tokenExpiredNotice').style.display = 'block';
        } 
        else {
            // Token is not expired, continue with the normal page flow
            setTimeout(() => {
                if (token && isTokenExpired(token)) {
                    document.getElementById('tokenExpiredNotice').style.display = 'block';
                }
            }, 30000); // 30 seconds
        }
    </script>
</body>
</html>
