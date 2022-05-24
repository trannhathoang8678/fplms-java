package plms.ManagementService.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plms.ManagementService.model.response.Response;
import plms.ManagementService.model.dto.GroupDTO;
import plms.ManagementService.service.constant.ServiceMessage;
import plms.ManagementService.service.constant.ServiceStatusCode;
import plms.ManagementService.repository.ClassRepository;
import plms.ManagementService.repository.GroupRepository;
import plms.ManagementService.repository.StudentGroupRepository;
import plms.ManagementService.repository.entity.Group;

@Service
public class GroupService {
    @Autowired
    ClassRepository classRepository;
    @Autowired
    StudentGroupRepository studentGroupRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    ModelMapper modelMapper;

    private static final Logger logger = LogManager.getLogger(GroupService.class);
    private static final String JOINED_OTHER_GROUP_MESSAGE = "Student already joined other group";
    //private static final String NOT_LEADER_MESSAGE = "Only leader is allowed to remove";
    private static final String NOT_IN_GROUP_MESSAGE = "Student not in group";
    private static final String GROUP_FULL_MESSAGE = "Group is full";
    private static final String ADD_STUDENT_TO_GROUP_MESSAGE = "Add student to group: ";
    private static final String REMOVE_STUDENT_FROM_GROUP_MESSAGE = "Remove student from group: ";
    private static final String GET_GROUP_IN_CLASS_MESSAGE = "Get group in class: ";

    @Transactional
    public Response<String> addStudentToGroup(Integer classId, Integer groupId, Integer studentId) {
        if (classId == null || groupId == null || studentId == null) {
            logger.warn("{}{}", ADD_STUDENT_TO_GROUP_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        if (classRepository.existsInClass(studentId, classId) == null ||
                groupRepository.isGroupExistsInClass(groupId, classId) == null) {
            logger.warn("{}{}", ADD_STUDENT_TO_GROUP_MESSAGE, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.NOT_FOUND_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
        } else if (groupRepository.findGroupByStudentIdAndClassId(studentId, classId) != null) {
            logger.warn("{}{}", ADD_STUDENT_TO_GROUP_MESSAGE, JOINED_OTHER_GROUP_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, JOINED_OTHER_GROUP_MESSAGE);
        } else if (groupRepository.getGroupLimitNumber(groupId) <= studentGroupRepository.getCurrentNumberOfMemberInGroup(groupId)) {
            logger.warn("{}{}", ADD_STUDENT_TO_GROUP_MESSAGE, GROUP_FULL_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, GROUP_FULL_MESSAGE);
        } else {
            studentGroupRepository.addStudentInGroup(studentId, groupId, classId);
            logger.info("{}{}", ADD_STUDENT_TO_GROUP_MESSAGE, ServiceMessage.SUCCESS_MESSAGE);
            return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
        }

    }

    @Transactional
    public Response<String> removeStudentFromGroup(Integer classId, Integer groupId, Integer studentId) {
        if (classId == null || groupId == null || studentId == null) {
            logger.warn("{}{}", REMOVE_STUDENT_FROM_GROUP_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        if (classRepository.existsInClass(studentId, classId) == null ||
                groupRepository.isGroupExistsInClass(groupId, classId) == null) {
            logger.warn("{}{}", REMOVE_STUDENT_FROM_GROUP_MESSAGE, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.NOT_FOUND_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
        } else if (!groupRepository.findGroupByStudentIdAndClassId(studentId, classId).equals(groupId)) {
            logger.warn("{}{}", REMOVE_STUDENT_FROM_GROUP_MESSAGE, NOT_IN_GROUP_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, NOT_IN_GROUP_MESSAGE);
        } else {
            studentGroupRepository.deleteStudentInGroup(studentId, classId);
            logger.info("{}{}", REMOVE_STUDENT_FROM_GROUP_MESSAGE, ServiceMessage.SUCCESS_MESSAGE);
            return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
        }

    }

    public Response<GroupDTO> getGroupByGroupIdAndClassId(Integer groupId, Integer classId) {
        if (classId == null || groupId == null) {
            logger.warn("{}{}", GET_GROUP_IN_CLASS_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        } else if (groupRepository.isGroupExistsInClass(groupId, classId) == null) {
            logger.warn("{}{}", GET_GROUP_IN_CLASS_MESSAGE, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.NOT_FOUND_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
        } else {
            Group group = groupRepository.getGroupById(groupId);
            GroupDTO groupDTO = modelMapper.map(group, GroupDTO.class);
            logger.info("{}{}", GET_GROUP_IN_CLASS_MESSAGE, ServiceMessage.SUCCESS_MESSAGE);
            return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE, groupDTO);
        }

    }


}
