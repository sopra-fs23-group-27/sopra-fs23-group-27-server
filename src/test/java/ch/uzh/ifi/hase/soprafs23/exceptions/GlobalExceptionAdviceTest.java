package ch.uzh.ifi.hase.soprafs23.exceptions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionAdviceTest {

    private final Logger log = LoggerFactory.getLogger(GlobalExceptionAdviceTest.class);

    @Mock
    private HttpServletRequest httpServletRequest;

    @Test
    public void testHandleTransactionSystemException() {
        GlobalExceptionAdvice exceptionAdvice = new GlobalExceptionAdvice();
        String expectedMessage = "test exception";
        Exception exception = new Exception(expectedMessage);

        ResponseStatusException responseStatusException = exceptionAdvice.handleTransactionSystemException(exception, httpServletRequest);

        assertEquals(HttpStatus.CONFLICT, responseStatusException.getStatus());
        assertEquals("409 CONFLICT \"test exception\"; nested exception is java.lang.Exception: test exception", responseStatusException.getMessage());
        assertEquals(exception, responseStatusException.getCause());
    }

    @Test
    public void testHandleException() {
        GlobalExceptionAdvice exceptionAdvice = new GlobalExceptionAdvice();
        String expectedMessage = "test exception";
        Exception exception = new Exception(expectedMessage);

        ResponseStatusException responseStatusException = exceptionAdvice.handleException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseStatusException.getStatus());
        assertEquals("500 INTERNAL_SERVER_ERROR \"test exception\"; nested exception is java.lang.Exception: test exception", responseStatusException.getMessage());
        assertEquals(exception, responseStatusException.getCause());
    }
}