package org.hackncrypt.userservice.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserLevelChange  {
    private Integer xp;
    private Long clan;
}
