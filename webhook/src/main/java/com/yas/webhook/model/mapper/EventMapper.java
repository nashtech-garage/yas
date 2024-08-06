package com.yas.webhook.model.mapper;

import com.yas.webhook.model.Event;
import com.yas.webhook.model.viewmodel.webhook.EventVm;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventMapper {

    EventVm toEventVm(Event event);

}
