package org.example.di;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;


public class BeanFactory {
    private final Set<Class<?>> preInstantiatedClazz;
    private final Map<Class<?>, Object> beans = new HashMap<>();

    public BeanFactory(Set<Class<?>> preInstantiatedClazz) {
        this.preInstantiatedClazz = preInstantiatedClazz;
        initialize();
    }

    private void initialize() {
        for (Class<?> clazz : preInstantiatedClazz) {
            Object instance = createInstance(clazz);
            beans.put(clazz, instance);
        }
    }

    // UserController, UserService
    private Object createInstance(Class<?> clazz) {
        // constructor
        Constructor<?> constructor = findConstructor(clazz);

        // parameter
        List<Object> parameters = new ArrayList<>();
        for (Class<?> typeClass : constructor.getParameterTypes()) {
            // userService
            parameters.add(getParameterByClass(typeClass));
        }

        // create instande
        try {
            return constructor.newInstance(parameters.toArray());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private Constructor<?> findConstructor(Class<?> clazz) {
        Constructor<?> constructor = BeanFactoryUtils.getInjectedConstructor(clazz);

        return Objects.nonNull(constructor) ? constructor : clazz.getConstructors()[0];
//        if(Objects.nonNull(constructor)) {
//            return constructor;
//        }
//        return clazz.getConstructors()[0];
    }

    private Object getParameterByClass(Class<?> typeClass) {
        Object instanceBean = getBean(typeClass);

        return Objects.nonNull(instanceBean) ? instanceBean : createInstance(typeClass);
//        if(Objects.nonNull(instanceBean)){
//            return instanceBean;
//        }
//        return createInstance(typeClass);
    }

    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }
}
