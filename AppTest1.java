package com.napier.sem;

import org.junit.jupiter.api.*;
import org.mockito.*;

import java.sql.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class AppTest {

    private App app;

    @BeforeEach
    void setup() {
        app = new App();
    }

    @Test
    void testDisconnectWhenConnectionIsNull() {
        assertDoesNotThrow(() -> app.disconnect());
    }

    @Test
    void testDisconnectClosesConnection() throws Exception {
        Connection mockCon = mock(Connection.class);

        // Inject mock connection using reflection since field is private
        var conField = App.class.getDeclaredField("con");
        conField.setAccessible(true);
        conField.set(app, mockCon);

        app.disconnect();

        verify(mockCon, times(1)).close();
    }

    @Test
    void testGetEmployeeFound() throws Exception {
        // Mock JDBC objects
        Connection mockCon = mock(Connection.class);
        Statement mockStmt = mock(Statement.class);
        ResultSet mockRs = mock(ResultSet.class);

        // Inject mock connection
        var conField = App.class.getDeclaredField("con");
        conField.setAccessible(true);
        conField.set(app, mockCon);

        when(mockCon.createStatement()).thenReturn(mockStmt);
        when(mockStmt.executeQuery(anyString())).thenReturn(mockRs);

        // Simulate 1 record returned
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getInt("emp_no")).thenReturn(123);
        when(mockRs.getString("first_name")).thenReturn("John");
        when(mockRs.getString("last_name")).thenReturn("Doe");

        Employee emp = app.getEmployee(123);

        assertNotNull(emp);
        assertEquals(123, emp.emp_no);
        assertEquals("John", emp.first_name);
        assertEquals("Doe", emp.last_name);
    }

    @Test
    void testGetEmployeeNotFound() throws Exception {
        Connection mockCon = mock(Connection.class);
        Statement mockStmt = mock(Statement.class);
        ResultSet mockRs = mock(ResultSet.class);

        var conField = App.class.getDeclaredField("con");
        conField.setAccessible(true);
        conField.set(app, mockCon);

        when(mockCon.createStatement()).thenReturn(mockStmt);
        when(mockStmt.executeQuery(anyString())).thenReturn(mockRs);

        // No rows returned
        when(mockRs.next()).thenReturn(false);

        Employee emp = app.getEmployee(999);

        assertNull(emp);
    }

    @Test
    void testGetEmployeeExceptionHandled() throws Exception {
        Connection mockCon = mock(Connection.class);

        var conField = App.class.getDeclaredField("con");
        conField.setAccessible(true);
        conField.set(app, mockCon);

        when(mockCon.createStatement()).thenThrow(new SQLException("DB error"));

        Employee emp = app.getEmployee(1);

        assertNull(emp);  // Method returns null on exception
    }

    @Test
    void testDisplayEmployeeOutput() {
        Employee emp = new Employee();
        emp.emp_no = 1;
        emp.first_name = "John";
        emp.last_name = "Smith";
        emp.title = "Engineer";
        emp.salary = 50000;
        emp.dept_name = "Development";
        emp.manager = "Alice";

        // Capture console output
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        app.displayEmployee(emp);

        String output = out.toString();

        assertTrue(output.contains("1 John Smith"));
        assertTrue(output.contains("Engineer"));
        assertTrue(output.contains("Salary:50000"));
        assertTrue(output.contains("Development"));
        assertTrue(output.contains("Manager: Alice"));
    }
}
