package fpt.plms.config;


import fpt.plms.model.response.Response;
import fpt.plms.service.exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public Response<String> handleUnwantedException(Exception e) {
        logger.error(e.getMessage());
        return new Response<>(500, "Unknown error");
    }

    @ExceptionHandler(ServiceException.class)
    public Response<String> handleCustomException(ServiceException e) {
        logger.warn("{}: {}", e.getCode(), e.getMessage());
        return new Response<>(e.getCode(), e.getMessage());
    }
}
