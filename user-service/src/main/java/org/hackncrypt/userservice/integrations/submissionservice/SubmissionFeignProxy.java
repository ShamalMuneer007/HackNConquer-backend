package org.hackncrypt.userservice.integrations.submissionservice;

import org.hackncrypt.userservice.model.dto.auth.OtpDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "submission",path = "/submission")
public interface SubmissionFeignProxy {

}
