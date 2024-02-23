package com.digiwin.code.coverage.backend.util;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 简单封装orika, 实现深度的BeanOfClasssA<->BeanOfClassB复制
 *
 * 不要使用Apache Common BeanUtils进行类复制，每次就行反射查询对象的属性列表, 非常缓慢.
 *
 * 注意: 需要参考本模块的POM文件，显式引用orika.
 */
public class OrikaMapperUtil {

    private static MapperFactory mapperFactory;

    /**
     * 默认字段实例集合
     */
    private static Map<String, MapperFacade> CACHE_MAPPER_FACADE_MAP = new ConcurrentHashMap<>();

    private OrikaMapperUtil() {
        super();
    }

    static {
         mapperFactory = new DefaultMapperFactory.Builder().build();
    }

    /**
     *
     * 简单的复制出新类型对象. 通过source.getClass() 获得源Class
     *
     * @param source
     * @param destinationClass
     * @param <S>
     * @param <D>
     * @return
     */
    public static <S, D> D map(S source, Class<D> destinationClass) {
        return mapperFactory.getMapperFacade().map(source, destinationClass);
    }


    /**
     * 映射实体（自定义配置）
     *
     * @param configMap 自定义配置
     * @return 映射类对象
     */
    public static <E, T> E map(T source, Class<E> destinationClass,  Map<String, String> configMap) {
        MapperFacade mapperFacade = getMapperFacade(destinationClass, source.getClass(), configMap);
        return mapperFacade.map(source, destinationClass);
    }

    /**
     * 映射实体（自定义配置）,source是map时，排除指定字段
     *
     * @param configMap 自定义配置
     * @return 映射类对象
     */
    public static <E, T> E map(T source, Class<E> destinationClass,  Map<String, String> configMap,List<String> excludeMapField) {
        MapperFacade mapperFacade = getMapperFacade(destinationClass, source.getClass(), configMap,excludeMapField);
        return mapperFacade.map(source, destinationClass);
    }
    /**
     * 获取自定义映射
     *
     * @param toClass 映射类
     * @param dataClass 数据映射类
     * @param configMap 自定义配置
     * @return 映射类对象
     */
    private static <E, T> MapperFacade getMapperFacade(Class<E> toClass, Class<T> dataClass, Map<String, String> configMap) {
        String mapKey = dataClass.getCanonicalName() + "_" + toClass.getCanonicalName();
        MapperFacade mapperFacade = CACHE_MAPPER_FACADE_MAP.get(mapKey);
        if (Objects.isNull(mapperFacade)) {
            ClassMapBuilder classMapBuilder = mapperFactory.classMap(dataClass, toClass);
            configMap.forEach(classMapBuilder::field);
            classMapBuilder.byDefault().register();
            mapperFacade = mapperFactory.getMapperFacade();
            CACHE_MAPPER_FACADE_MAP.put(mapKey, mapperFacade);
        }
        return mapperFacade;
    }

    /**
     * 获取自定义映射
     *
     * @param toClass 映射类
     * @param dataClass 数据映射类
     * @param configMap 自定义配置
     * @return 映射类对象
     */
    private static <E, T> MapperFacade getMapperFacade(Class<E> toClass, Class<T> dataClass, Map<String, String> configMap,List<String> excludeMapField) {
        String mapKey = dataClass.getCanonicalName() + "_" + toClass.getCanonicalName();
        MapperFacade mapperFacade = CACHE_MAPPER_FACADE_MAP.get(mapKey);
        if (Objects.isNull(mapperFacade)) {
            ClassMapBuilder classMapBuilder = mapperFactory.classMap(dataClass, toClass);
            //排除字段
                excludeMapField.forEach((name)->{
                    classMapBuilder.fieldMap(name).exclude().add();
                });
            configMap.forEach(classMapBuilder::field);
            classMapBuilder.byDefault().register();
            mapperFacade = mapperFactory.getMapperFacade();
            CACHE_MAPPER_FACADE_MAP.put(mapKey, mapperFacade);
        }
        return mapperFacade;
    }

    /**
     * 极致性能的复制出新类型对象. 预先通过BeanMapper.getType() 静态获取并缓存Type类型，在此处传入
     *
     * @param source
     * @param sourceType
     * @param destinationType
     * @param <S>
     * @param <D>
     * @return
     */
    public static <S, D> D map(S source, Type<S> sourceType, Type<D> destinationType) {
        return mapperFactory.getMapperFacade().map(source, sourceType, destinationType);
    }

    /**
     * 简单的复制出新对象列表到ArrayList
     *
     * 不建议使用mapper.mapAsList(Iterable<S>,Class<D>)接口, sourceClass需要反射，实在有点慢
     *
     * @param sourceList
     * @param sourceClass
     * @param destinationClass
     * @param <S>
     * @param <D>
     * @return
     */
    public static <S, D> List<D> mapList(Iterable<S> sourceList, Class<S> sourceClass, Class<D> destinationClass) {
        return mapperFactory.getMapperFacade().mapAsList(sourceList, TypeFactory.valueOf(sourceClass), TypeFactory.valueOf(destinationClass));
    }

    /**
     * 极致性能的复制出新类型对象到ArrayList.
     *
     * 预先通过BeanMapper.getType() 静态获取并缓存Type类型，在此处传入
     */
    public static <S, D> List<D> mapList(Iterable<S> sourceList, Type<S> sourceType, Type<D> destinationType) {
        return mapperFactory.getMapperFacade().mapAsList(sourceList, sourceType, destinationType);
    }

    /**
     * 简单复制出新对象列表到数组
     *
     * 通过source.getComponentType() 获得源Class
     */
    public static <S, D> D[] mapArray(final D[] destination, final S[] source, final Class<D> destinationClass) {
        return mapperFactory.getMapperFacade().mapAsArray(destination, source, destinationClass);
    }

    /**
     * 极致性能的复制出新类型对象到数组
     *
     * 预先通过BeanMapper.getType() 静态获取并缓存Type类型，在此处传入
     */
    public static <S, D> D[] mapArray(D[] destination, S[] source, Type<S> sourceType, Type<D> destinationType) {
        return mapperFactory.getMapperFacade().mapAsArray(destination, source, sourceType, destinationType);
    }

    /**
     * 预先获取orika转换所需要的Type，避免每次转换.
     */
    public static <E> Type<E> getType(final Class<E> rawType) {
        return TypeFactory.valueOf(rawType);
    }
}
