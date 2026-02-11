package com.yas.webhook.model.viewmodel.webhook;

import com.yas.webhook.model.enums.EventName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventVm {
    long id;
    EventName name;
}
