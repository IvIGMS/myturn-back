package com.ivanfrias.myturn.common.exceptions.mappers;

import com.ivanfrias.myturn.model.SubscriptionDTO;
import com.ivanfrias.myturn.subscriptions.dao.models.entities.SubscriptionEntity;
import jakarta.annotation.PostConstruct;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

@Configuration
public class ModelMapperConfig {

    private final Converter<ZonedDateTime, OffsetDateTime> zonedToOffset = ctx -> {
        ZonedDateTime source = ctx.getSource();
        return (source != null) ? source.toOffsetDateTime() : null;
    };

    private final Converter<OffsetDateTime, ZonedDateTime> offsetToZoned = ctx -> {
        OffsetDateTime source = ctx.getSource();
        return (source != null) ? source.toZonedDateTime() : null;
    };

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        configureUserMapping(mapper);
        return mapper;
    }

    private void configureUserMapping(ModelMapper mapper) {
        mapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setSkipNullEnabled(true);

        mapper.addConverter(zonedToOffset);
        mapper.addConverter(offsetToZoned);

//        mapper.typeMap(SubscriptionEntity.class, SubscriptionDTO.class).addMappings(m -> {
//            m.map(src -> src.getUser().getId(), SubscriptionDTO::setUserId);
//        });
    }

}