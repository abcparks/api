package cn.alex.util.beanutil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Zhou Xiaoxiang
 * @since 1.0
 */
public class BeanUtil {

    private static Logger logger = LoggerFactory.getLogger(BeanUtil.class);
    private static Map<Class, List<FieldDesc>> cache = new ConcurrentHashMap<>();

    public BeanUtil() {
    }

    public static Map<String, Object> beanToMapCommon(Object source) throws Exception {
        if (source == null) {
            logger.debug("Source object is null");
            return null;
        } else {
            Map<String, Object> mapRet = new HashMap();
            List<TempBean> list = (new BeanUtil()).findSourceMethod(source);
            Iterator var3 = list.iterator();

            while (var3.hasNext()) {
                TempBean tb = (TempBean) var3.next();
                try {
                    Object value = tb.getO().getClass().getMethod(tb.getFieldDesc().getGetName()).invoke(tb.getO());
                    String fieldName = tb.getFieldDesc().getFieldName();
                    mapRet.put(fieldName, value);
                } catch (NoSuchMethodException var7) {
                    logger.debug(tb.getFieldDesc().getSetName() + " is not exist, not covert!");
                } catch (SecurityException var8) {
                    logger.debug(tb.getFieldDesc().getSetName() + " is not exist, not covert!");
                }
            }
            return mapRet;
        }
    }

    public static Map<String, Object> beanToMap(Object source) throws Exception {
        if (source == null) {
            logger.debug("Source object is null");
            return null;
        } else {
            Map<String, Object> mapRet = new HashMap();
            List<TempBean> list = (new BeanUtil()).findSourceMethod(source, (List) null);
            Iterator var3 = list.iterator();

            while (var3.hasNext()) {
                TempBean tb = (TempBean) var3.next();
                try {
                    Object value = tb.getO().getClass().getMethod(tb.getFieldDesc().getGetName()).invoke(tb.getO());
                    String fieldName = tb.getFieldDesc().getGetName();
                    mapRet.put(fieldName, value);
                } catch (NoSuchMethodException var7) {
                    logger.debug(tb.getFieldDesc().getSetName() + " is not exist, not covert!");
                } catch (SecurityException var8) {
                    logger.debug(tb.getFieldDesc().getSetName() + " is not exist, not covert!");
                }
            }
            return mapRet;
        }
    }

    public static <T> List<T> copyPropertiesList(List<?> source, Class<T> clazz) {
        List listRet = new ArrayList();
        if (source != null && source.size() > 0) {
            for (int i = 0; i < source.size(); ++i) {
                listRet.add(copyPropertiesDeep(source.get(i), clazz));
            }
        } else {
            logger.debug("Source List is empty");
        }
        return listRet;
    }

    public static <T> Optional<T> copyPropertiesDeep(Optional<?> source, Class<T> clazz) {
        if (source.isPresent()) {
            T t = copyPropertiesDeep(source.get(), clazz);
            return Optional.ofNullable(t);
        } else {
            return Optional.ofNullable((T) null);
        }
    }

    public static <T> T copyPropertiesDeep(Object source, Class<T> clazz) {
        if (source == null) {
            return null;
        } else {
            T target = null;
            try {
                target = clazz.newInstance();
            } catch (InstantiationException var4) {
                logger.error("init target object error");
            } catch (IllegalAccessException var5) {
                logger.error("init target object error");
            }
            copyPropertiesDeep(source, target);
            return target;
        }
    }

    public static void copyPropertiesDeep(Object source, Object target) {
        if (target == null) {
            logger.debug("Target object is null");
        } else if (source == null) {
            logger.debug("Source object is null");
        } else {
            try {
                Map<String, TempBean> tbSourceMap = (new BeanUtil()).findSourceMethodAndTanslateGetNameMap(source);
                List<TempBean> listTarget = (new BeanUtil()).findSourceMethod(target, (List) null);
                Iterator var4 = listTarget.iterator();

                while (var4.hasNext()) {
                    TempBean tempBean = (TempBean) var4.next();

                    try {
                        TempBean tempBeanSource = (TempBean) tbSourceMap.get(tempBean.getFieldDesc().getGetName());
                        if (tempBeanSource != null) {
                            Method method = target.getClass().getMethod(tempBean.getFieldDesc().getSetName(), tempBean.getFieldDesc().getClazz());
                            Object value = tempBeanSource.getO().getClass().getMethod(tempBeanSource.getFieldDesc().getGetName()).invoke(tempBeanSource.getO());
                            if (value != null) {
                                method.invoke(target, value);
                            }
                        }
                    } catch (NoSuchMethodException var9) {
                        logger.debug(tempBean.getFieldDesc().getSetName() + " is not exist, not covert!");
                    } catch (SecurityException var10) {
                        logger.debug(tempBean.getFieldDesc().getSetName() + " is not exist, not covert!");
                    }
                }
            } catch (Exception var11) {
                logger.error("copy occur error: {}", var11.getMessage());
            }
        }
    }

    protected Map<String, TempBean> findSourceMethodAndTanslateGetNameMap(Object source) throws Exception {
        List<TempBean> list = this.findSourceMethod(source, (List) null);
        Map<String, TempBean> retMap = new HashMap();
        Iterator var4 = list.iterator();

        while (var4.hasNext()) {
            TempBean tb = (TempBean) var4.next();
            retMap.put(tb.getFieldDesc().getGetName(), tb);
        }
        return retMap;
    }

    protected List<TempBean> findSourceMethod(Object source, List<TempBean> list) throws Exception {
        if (list == null) {
            list = new ArrayList();
        }

        Class<?> clazzSource = source.getClass();
        List<FieldDesc> descList = new ArrayList();
        Field field;
        if (cache.containsKey(source.getClass())) {
            descList = (List) cache.get(source.getClass());
        } else {
            List<Field> sourceFields = this.getAllFields(clazzSource, (List) null);

            for (int i = 0; i < sourceFields.size(); ++i) {
                field = (Field) sourceFields.get(i);
                if (this.isDirectConvert(field.getType())) {
                    ((List) descList).add(new FieldDesc(field));
                } else if (!Collection.class.isAssignableFrom(field.getType())) {
                    ((List) descList).add(new FieldDesc(field));
                }
            }
            cache.put(source.getClass(), descList);
        }

        Iterator var9 = ((List) descList).iterator();

        while (var9.hasNext()) {
            FieldDesc fieldDesc = (FieldDesc) var9.next();
            if (this.isDirectConvert(fieldDesc.getClazz())) {
                TempBean tempBean = new TempBean(fieldDesc, source);
                ((List) list).add(tempBean);
            } else {
                field = source.getClass().getDeclaredField(fieldDesc.getFieldName());
                Object nextO = this.getFieldObject(field, source);
                if (nextO != null) {
                    this.findSourceMethod(nextO, (List) list);
                }
            }
        }
        return (List) list;
    }

    protected List<TempBean> findSourceMethod(Object source) {
        List<TempBean> list = new ArrayList();
        Class<?> clazzSource = source.getClass();
        List<Field> sourceFields = this.getAllFields(clazzSource, (List) null);
        Iterator var5 = sourceFields.iterator();

        while (var5.hasNext()) {
            Field field = (Field) var5.next();
            list.add(new TempBean(new FieldDesc(field), source));
        }
        return list;
    }

    private boolean isDirectConvert(Class<?> clazz) {
        return String.class.equals(clazz) || Date.class.equals(clazz) || java.sql.Date.class.equals(clazz) || Number.class.isAssignableFrom(clazz) || Boolean.class.equals(clazz) || clazz.isPrimitive() || clazz.isEnum() || Class.class.equals(clazz);
    }

    private List<Field> getAllFields(Class<?> clazz, List<Field> list) {
        if (list == null) {
            list = new ArrayList();
        }

        Field[] fields = clazz.getDeclaredFields();
        Field[] var4 = fields;
        int var5 = fields.length;

        for (int var6 = 0; var6 < var5; ++var6) {
            Field field = var4[var6];
            ((List) list).add(field);
        }

        if (!"Object".equals(clazz.getSuperclass().getSimpleName())) {
            this.getAllFields(clazz.getSuperclass(), (List) list);
        }

        return (List) list;
    }

    private Object getFieldObject(Field field, Object source) throws InvocationTargetException, IllegalAccessException {
        String fieldName = field.getName();
        Method method = null;

        try {
            method = source.getClass().getMethod(JavaBeansUtil.getGetterMethodName(fieldName, field.getType().getSimpleName()));
        } catch (NoSuchMethodException var6) {
            return null;
        }

        Object o = method.invoke(source);
        return o;
    }

    protected class FieldDesc {
        private String fieldName;
        private String getName;
        private String setName;
        private Class<?> clazz;

        public FieldDesc(Field field) {
            this.fieldName = field.getName();
            this.getName = JavaBeansUtil.getGetterMethodName(this.fieldName, field.getType().getSimpleName());
            this.setName = JavaBeansUtil.getSetterMethodName(this.fieldName);
            this.clazz = field.getType();
        }

        public String getFieldName() {
            return this.fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getGetName() {
            return this.getName;
        }

        public void setGetName(String getName) {
            this.getName = getName;
        }

        public String getSetName() {
            return this.setName;
        }

        public void setSetName(String setName) {
            this.setName = setName;
        }

        public Class<?> getClazz() {
            return this.clazz;
        }

        public void setClazz(Class<?> clazz) {
            this.clazz = clazz;
        }
    }

    protected class TempBean {
        FieldDesc fieldDesc;
        private Object o;

        public TempBean(FieldDesc fieldDesc, Object o) {
            this.fieldDesc = fieldDesc;
            this.o = o;
        }

        public FieldDesc getFieldDesc() {
            return this.fieldDesc;
        }

        public void setFieldDesc(FieldDesc fieldDesc) {
            this.fieldDesc = fieldDesc;
        }

        public Object getO() {
            return this.o;
        }

        public void setO(Object o) {
            this.o = o;
        }
    }
}

