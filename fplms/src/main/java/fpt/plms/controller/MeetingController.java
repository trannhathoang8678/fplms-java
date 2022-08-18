package fpt.plms.controller;

import java.sql.Timestamp;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonFormat;

import fpt.plms.config.interceptor.GatewayConstant;
import fpt.plms.model.dto.MeetingDTO;
import fpt.plms.model.response.Response;
import fpt.plms.service.AuthenticationService;
import fpt.plms.service.MeetingService;
import fpt.plms.service.NotificationService;
import fpt.plms.service.constant.ServiceStatusCode;

@RestController
@RequestMapping("/api/management/meetings")
public class MeetingController {
	@Autowired
	MeetingService meetingService;
	@Autowired
	AuthenticationService authenticationService;
	@Autowired
	NotificationService notificationService;

	@GetMapping
	public Response<Set<MeetingDTO>> getMeetingInGroup(@RequestParam(required = false) Integer classId,
			@RequestParam(required = false) Integer groupId, @RequestAttribute(required = false) String userRole,
			@RequestAttribute(required = false) String userEmail,
			@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "Asia/Ho_Chi_Minh")
			@RequestParam(required = false, name = "startDate") Timestamp startDate,
			@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "Asia/Ho_Chi_Minh")
			@RequestParam(required = false, name = "endDate") Timestamp endDate) {
		if (userRole.contains(GatewayConstant.ROLE_LECTURER)) {
			return meetingService.getMeetingInGroupByLecturer(classId, groupId, startDate, endDate, userEmail);
		}
		if (userRole.contains(GatewayConstant.ROLE_STUDENT)) {
			return meetingService.getMeetingInGroupByStudent(classId, groupId, startDate, endDate, userEmail);
		}
		return new Response<>(403, "Not have role access");
	}

	@GetMapping("/{meetingId}")
	public Response<MeetingDTO> getMeetingById(@PathVariable Integer meetingId,
			@RequestAttribute(required = false) String userRole,
			@RequestAttribute(required = false) String userEmail) {
		if (userRole.contains(GatewayConstant.ROLE_LECTURER)) {
			return meetingService.getMeetingDetailByLecturer(userEmail, meetingId);
		}
		if (userRole.contains(GatewayConstant.ROLE_STUDENT)) {
			return meetingService.getMeetingDetailByStudent(userEmail, meetingId);
		}
		return new Response<>(403, "Not have role access");
	}


	@PostMapping
	public Response<MeetingDTO> scheduleMeetingByLecturer(@RequestBody MeetingDTO meetingDTO, @RequestAttribute(required = false) String userEmail) {
		meetingDTO.setLecturerId(authenticationService.getLectureIdByEmail(userEmail));
		Response<MeetingDTO> response = meetingService.scheduleMeetingByLecturer(meetingDTO);
		if (response.getCode().equals(ServiceStatusCode.OK_STATUS)) {
			notificationService.sendMeetingNotification(response.getData());
		}
		return response;
	}

	@PutMapping
	public Response<Void> updateMeetingByLecturer(@RequestBody MeetingDTO meetingDTO, @RequestAttribute(required = false) String userEmail) {
		meetingDTO.setLecturerId(authenticationService.getLectureIdByEmail(userEmail));
		Response<Void> response = meetingService.updateMeetingByLecturer(meetingDTO);
		if (response.getCode().equals(ServiceStatusCode.OK_STATUS)) {
			notificationService.sendMeetingNotification(meetingDTO);
		}
		return response;
	}

	@DeleteMapping
	public Response<Void> deleteMeetingByLecturer(@RequestParam Integer meetingId,@RequestAttribute(required = false) String userEmail){
		return meetingService.deleteMeetingByLecturer(authenticationService.getLectureIdByEmail(userEmail),meetingId);
	}
}
