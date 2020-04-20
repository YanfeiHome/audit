package com.jn.audit.core.filter;

import com.jn.audit.core.AuditRequest;
import com.jn.audit.core.AuditRequestFilter;
import com.jn.audit.core.annotation.Audit;
import com.jn.langx.util.collection.ConcurrentReferenceHashMap;
import com.jn.langx.util.reflect.Reflects;
import com.jn.langx.util.reflect.reference.ReferenceType;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

/**
 * 由方法上的 @Audit 注解来决定该方法执行是否要被审计
 *
 * @param <AuditedRequest>
 */
public class MethodAuditAnnotationFilter<AuditedRequest> implements AuditRequestFilter<AuditedRequest, Method> {
    private boolean auditIfAnnotationNotExists = true;
    private ConcurrentReferenceHashMap<Method, Boolean> cache = new ConcurrentReferenceHashMap<Method, Boolean>(100, 0.75f, Runtime.getRuntime().availableProcessors(), ReferenceType.SOFT, ReferenceType.STRONG);

    @Override
    public boolean accept(AuditRequest<AuditedRequest, Method> wrappedRequest) {
        Method method = wrappedRequest.getRequestContext();
        Boolean auditIt = cache.get(method);
        if (auditIt == null) {
            auditIt = compute(method);
            cache.put(method, auditIt);
        }
        return auditIt;
    }

    private boolean compute(AnnotatedElement annotatedElement) {
        Audit audit = Reflects.getAnnotation(annotatedElement, Audit.class);
        if (audit == null) {
            if (annotatedElement instanceof Method) {
                compute(((Method) annotatedElement).getDeclaringClass());
            }
            return auditIfAnnotationNotExists;
        }
        return audit.enable();
    }

    public boolean isAuditIfAnnotationNotExists() {
        return auditIfAnnotationNotExists;
    }

    public void setAuditIfAnnotationNotExists(boolean auditIfAnnotationNotExists) {
        this.auditIfAnnotationNotExists = auditIfAnnotationNotExists;
    }
}
