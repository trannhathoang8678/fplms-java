package fpt.plms.service;

import fpt.plms.config.WebMvcConfiguration;
import fpt.plms.config.interceptor.GatewayConstant;
import fpt.plms.model.dto.EmailVerifyDTO;
import fpt.plms.model.request.GoogleAuthRequest;
import fpt.plms.model.response.GoogleInfoResponse;
import fpt.plms.model.response.GoogleTokenResponse;
import fpt.plms.repository.LecturerRepository;
import fpt.plms.repository.StudentRepository;
import fpt.plms.repository.entity.Lecturer;
import fpt.plms.repository.entity.Student;
import fpt.plms.service.constant.ServiceMessage;
import fpt.plms.service.constant.ServiceStatusCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import fpt.plms.model.request.CreateUserRequest;
import fpt.plms.model.response.Response;
import org.springframework.web.reactive.function.BodyInserters;

@Service
public class AuthenticationService {
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    LecturerRepository lecturerRepository;
    @Autowired
    GoogleAuthRequest googleAuthRequest;
    @Value("${auth.token.url}")
    private String getTokenUrl;
    @Value("${auth.info.url}")
    private String infoTokenUrl;
    @Value("${auth.valid.url}")
    private String validTokenUrl;
    private static final Logger logger = LogManager.getLogger(AuthenticationService.class);
    private static final String CREATE_USER_MESSAGE = "Create user: ";
    private static final String VALIDATE_GG_TOKEN_MESSAGE = "Validate google token: ";

    public Response<Void> createUser(CreateUserRequest createUserRequest) {
        if (createUserRequest.getIsLecturer()) {
            if (lecturerRepository.existsByEmail(createUserRequest.getEmail())) {
                logger.warn("{}{}", CREATE_USER_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
                return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            }
            lecturerRepository.save(new Lecturer(createUserRequest.getName(), createUserRequest.getEmail(), createUserRequest.getImageUrl()));
            logger.info("{}{}", CREATE_USER_MESSAGE, ServiceMessage.SUCCESS_MESSAGE);
            return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
        }
        if (studentRepository.existsByEmail(createUserRequest.getEmail())) {
            logger.warn("{}{}", CREATE_USER_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        studentRepository.save(new Student(createUserRequest.getName(), createUserRequest.getEmail(), createUserRequest.getCode(), createUserRequest.getImageUrl()));
        logger.info("{}{}", CREATE_USER_MESSAGE, ServiceMessage.SUCCESS_MESSAGE);
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
    }

    public GoogleTokenResponse validateGoogleToken(String code) {
        if (code == null) {
            logger.warn("{}{}", VALIDATE_GG_TOKEN_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return null;
        }
        GoogleTokenResponse response = WebMvcConfiguration.getWebClientBuilder().build().post().uri(getTokenUrl).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(new GoogleAuthRequest(googleAuthRequest,code))).retrieve().bodyToMono(GoogleTokenResponse.class).block();
        logger.info("{}{}","Access token: ", response.toString());
        logger.info("{}{}","Token info: ",getGoogleInfoResponse(response.getIdToken()));
        return response;
    }
    private String getGoogleInfoResponse(String token) {
        return WebMvcConfiguration.getWebClientBuilder().build().get().uri(infoTokenUrl+token)
                .retrieve().bodyToMono(String.class).block();
    }

    public Integer getLectureIdByEmail(String email) {
        return lecturerRepository.findLecturerIdByEmail(email);
    }

}
