package com.yas.commonlibrary.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * A generic base interface for mapping between model and view model objects.
 *
 * @param <M>    The entity type that represents the model.
 * @param <V> The type that represents the view model.
 */
public interface BaseMapper<M, V> {

    /**
     * Converts the provided view model to its corresponding model entity.
     *
     * @param vm The view model object to convert.
     * @return The model entity corresponding to the view model.
     */
    M toModel(V vm);

    /**
     * Converts the provided model entity to its corresponding view model.
     *
     * @param m The model entity to convert.
     * @return The view model corresponding to the model entity.
     */
    V toVm(M m);

    /**
     * Partially updates the provided model entity with values from the given view model.
     * Fields in the view model that are null will be ignored.
     *
     * @param m    The model entity to update.
     * @param v The view model with updated values.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget M m, V v);

}
