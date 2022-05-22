package plms.ManagementService.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plms.ManagementService.controller.response.Response;
import plms.ManagementService.controller.response.StudentInClassResponse;
import plms.ManagementService.dto.GroupDTO;
import plms.ManagementService.dto.StudentDTO;
import plms.ManagementService.interceptor.GatewayConstant;
import plms.ManagementService.repository.ClassRepository;
import plms.ManagementService.repository.GroupRepository;
import plms.ManagementService.repository.StudentGroupRepository;
import plms.ManagementService.repository.StudentRepository;
import plms.ManagementService.repository.entity.Student;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ClassService {
    @Autowired
    ClassRepository classRepository;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    StudentGroupRepository studentGroupRepository;
    @Autowired
    ModelMapper modelMapper;

    private static final Logger logger = LogManager.getLogger(ClassService.class);
    private static final String SUCCESS_MESSAGE = "Success";
    private static final String ID_NOT_EXIST_MESSAGE = "Id is not exist";
    private static final String INVALID_ARGUMENT_MESSAGE = "Invalid argument";
    private static final String GET_STUDENT_IN_CLASS_MESSAGE = "Get student in class: ";
    private static final String REMOVE_STUDENT_IN_CLASS_MESSAGE = "Remove student in class: ";
    private static final String CHANGE_STUDENT_GROUP_MESSAGE = "Change student group: ";

    public Response<Set<StudentInClassResponse>> getStudentInClass(Integer classId) {
        if (classId == null) {
            logger.warn("{}{}", GET_STUDENT_IN_CLASS_MESSAGE, INVALID_ARGUMENT_MESSAGE);
            return new Response<>(GatewayConstant.BAD_REQUEST_STATUS, INVALID_ARGUMENT_MESSAGE);
        }
        Set<Student> studentSet = classRepository.findOneById(classId).getStudentSet();
        if (studentSet == null) {
            logger.warn("{}{}", GET_STUDENT_IN_CLASS_MESSAGE, ID_NOT_EXIST_MESSAGE);
            return new Response<>(GatewayConstant.NOT_FOUND_STATUS, ID_NOT_EXIST_MESSAGE);
        } else {
            Set<StudentInClassResponse> studentInClassResponseSet = studentSet.stream()
                    .map(student -> modelMapper.map(student, StudentInClassResponse.class)).collect(Collectors.toSet());
            studentInClassResponseSet.stream()
                    .forEach(studentInClassResponse -> {
                        studentInClassResponse
                                .setGroupNumber(studentRepository.findGroupByStudentIdAndClassId(studentInClassResponse.getId(), classId));
                        studentInClassResponse
                                .setVote(studentRepository.findStudentVoteInClass(studentInClassResponse.getId(), classId));
                    });
            logger.info("{}{}", GET_STUDENT_IN_CLASS_MESSAGE, SUCCESS_MESSAGE);
            return new Response<>(GatewayConstant.OK_STATUS, SUCCESS_MESSAGE, studentInClassResponseSet);
        }
    }

    @Transactional
    public Response<String> removeStudentInClass(Integer studentId, Integer classId) {
        if (studentId == null) {
            logger.warn("{}{}", GET_STUDENT_IN_CLASS_MESSAGE, INVALID_ARGUMENT_MESSAGE);
            return new Response<>(GatewayConstant.BAD_REQUEST_STATUS, INVALID_ARGUMENT_MESSAGE);
        }
        if (classRepository.existsInClass(studentId, classId) != null) //exist
        {
            classRepository.deleteStudentInClass(studentId, classId);
            studentGroupRepository.deleteStudentInGroup(studentId, classId);
            logger.info("{}{}", REMOVE_STUDENT_IN_CLASS_MESSAGE, SUCCESS_MESSAGE);
            return new Response<>(GatewayConstant.OK_STATUS, SUCCESS_MESSAGE);
        } else {
            logger.warn("{}{}", REMOVE_STUDENT_IN_CLASS_MESSAGE, ID_NOT_EXIST_MESSAGE);
            return new Response<>(GatewayConstant.NOT_FOUND_STATUS, ID_NOT_EXIST_MESSAGE);
        }
    }

    public Response<String> changeStudentGroup(Integer classId, Integer studentId, Integer groupNumber) {
        if (classRepository.existsInClass(studentId, classId) != null) //exist
        {
            studentGroupRepository.updateStudentGroup(studentId, classId, groupNumber);
            logger.info("{}{}", CHANGE_STUDENT_GROUP_MESSAGE, SUCCESS_MESSAGE);
            return new Response<>(GatewayConstant.OK_STATUS, SUCCESS_MESSAGE);
        } else {
            logger.warn("{}{}", CHANGE_STUDENT_GROUP_MESSAGE, ID_NOT_EXIST_MESSAGE);
            return new Response<>(GatewayConstant.NOT_FOUND_STATUS, ID_NOT_EXIST_MESSAGE);
        }
    }
}
