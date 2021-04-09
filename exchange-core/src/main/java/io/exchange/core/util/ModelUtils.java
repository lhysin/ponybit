package io.exchange.core.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("unused")
@Component
@Slf4j
public class ModelUtils {

    private static ModelMapper modelMapper; 

    @SuppressWarnings("static-access")
    @Autowired(required = true)
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public static <D, T> List<D> mapAll(Collection<T> entityList, Class<D> outClazz) {
        return entityList.stream()
                .map(entity -> ModelUtils.map(entity, outClazz))
                .collect(Collectors.toList());
    }

    public static <D> D map(Object source, Class<D> destinationType) {
        return modelMapper.map(source, destinationType);
    }

    private static Gson gson;

    @SuppressWarnings("static-access")
    @Autowired(required = true)
    public void setGson(Gson gson) {
        this.gson = gson;
    }

    public static String toJsonString(Object value) {
        return gson.toJson(value);
    }

    public static JsonElement toJsonTree(Object value) {
        return gson.toJsonTree(value);
    }

    public static Map<?, ?> toJsonMap(String json){
        return gson.fromJson(json, Map.class);
    }
}
