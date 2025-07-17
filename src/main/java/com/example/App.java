package com.example;

import java.sql.*;
import javax.servlet.http.*;
import java.io.*;
import java.net.*;
import com.fasterxml.jackson.databind.ObjectMapper;

public class App {
    public static void main(String[] args) throws Exception {
        // A1: Injection (SQL Injection)
        String userInput = "' OR '1'='1";
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/test", "root", "password");
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE username = '" + userInput + "'");

        // A2: Broken Auth (Simulated with insecure cookie)
        HttpServletResponse response = null;
        Cookie session = new Cookie("session_id", "admin123");
        response.addCookie(session);

        // A3: Sensitive Data Exposure (plaintext password)
        String password = "mysecretpassword";
        System.out.println("Password: " + password);

        // A4: XML External Entity (XXE)
        String xml = "<?xml version=\"1.0\"?><!DOCTYPE foo [ <!ELEMENT foo ANY > <!ENTITY xxe SYSTEM \"file:///etc/passwd\" >]><foo>&xxe;</foo>";
        javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
        factory.setExpandEntityReferences(true);
        javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
        builder.parse(new ByteArrayInputStream(xml.getBytes()));

        // A5: Broken Access Control (simulated)
        boolean isAdmin = true; // Hardcoded for demo
        if (isAdmin) {
            System.out.println("Access to admin panel granted.");
        }

        // A6: Security Misconfig (e.g., exposed admin endpoint)
        System.out.println("Visit http://localhost:8080/admin");

        // A7: XSS
        String userComment = "<script>alert('XSS')</script>";
        System.out.println("User Comment: " + userComment);

        // A8: Insecure Deserialization
        String json = "{\"class\":\"java.lang.Runtime\",\"exec\":\"calc\"}";
        ObjectMapper mapper = new ObjectMapper();
        Object obj = mapper.readValue(json, Object.class);

        // A9: Using Components with Known Vulnerabilities (handled via pom.xml)

        // A10: Insufficient Logging (no logs on auth failures)
    }
}
