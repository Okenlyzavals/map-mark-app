package by.baranouski.mapphotoapp.markservice.mapper;

import _by.baranouski.mapphotoapp.api.model.MarkRequestDto;
import _by.baranouski.mapphotoapp.api.model.MarkResponseDto;
import _by.baranouski.mapphotoapp.api.model.PointDto;
import by.baranouski.mapphotoapp.markservice.model.Mark;
import by.baranouski.mapphotoapp.markservice.model.Point;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface MarkMapper {
    @Mapping(source = "location", target = "location", qualifiedByName = "dtoToPoint")
    Mark toMark(MarkRequestDto markRequestDto);
    @Mapping(source = "location", target = "location", qualifiedByName = "pointToDto")
    MarkResponseDto toResponse(Mark mark);
    List<MarkResponseDto> toResponseList(List<Mark> marks);


    @Named("dtoToPoint")
    static Point toPoint(PointDto pointDto){
        return new Point(pointDto.getX().doubleValue(), pointDto.getY().doubleValue());
    }

    @Named("pointToDto")
    static PointDto toDto(Point point){
        return new PointDto()
                .x(BigDecimal.valueOf(point.getCoordinates()[0]))
                .y(BigDecimal.valueOf(point.getCoordinates()[1]));
    }
}
