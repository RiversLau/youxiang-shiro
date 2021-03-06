package com.youxiang.shiro.subject;

import com.youxiang.shiro.util.CollectionUtils;
import com.youxiang.shiro.util.StringUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

/**
 * Author: RiversLau
 * Date: 2018/1/12 14:13
 */
public class SimplePrincipalCollection implements MutablePrincipalCollection {

    private Map<String, Set> realmPrincipals;
    private transient String cachedToString;

    public SimplePrincipalCollection() {
    }

    public SimplePrincipalCollection(Object principal, String realmName) {
        if (principal instanceof Collection) {
            addAll((Collection) principal, realmName);
        } else {
            add(principal, realmName);
        }
    }

    public SimplePrincipalCollection(Collection principals, String realmName) {
        addAll(principals, realmName);
    }

    public SimplePrincipalCollection(PrincipalCollection principals) {
        addAll(principals);
    }

    public Object getPrimaryPrincipal() {
        if (isEmpty()) {
            return null;
        }
        return iterator().next();
    }

    protected Collection<Object> getPrincipalsLazy(String realmName) {
        if (realmPrincipals == null) {
            realmPrincipals = new LinkedHashMap<String, Set>();
        }
        Set principals = realmPrincipals.get(realmName);
        if (principals == null) {
            principals = new LinkedHashSet();
            realmPrincipals.put(realmName, principals);
        }
        return principals;
    }

    public void add(Object principal, String realmName) {
        if (realmName == null) {
            throw new IllegalArgumentException("realmName cannot be null.");
        }
        if (principal == null) {
            throw new IllegalArgumentException("principal cannot be null.");
        }
        this.cachedToString = null;
        getPrincipalsLazy(realmName).add(principal);
    }

    public void addAll(Collection principals, String realmName) {
        if (realmName == null) {
            throw new IllegalArgumentException("realmName cannot be null.");
        }
        if (principals == null) {
            throw new IllegalArgumentException("principals cannot be null.");
        }
        if (principals.isEmpty()) {
            throw new IllegalArgumentException("principals cannot be empty.");
        }
        this.cachedToString = null;
        getPrincipalsLazy(realmName).addAll(principals);
    }

    public void addAll(PrincipalCollection principals) {
        if (principals.getRealmNames() != null) {
            for (String realmName : principals.getRealmNames()) {
                for (Object principal : principals.fromRealm(realmName)) {
                    add(principal, realmName);
                }
            }
        }
    }

    public <T> T oneByType(Class<T> type) {
        if (realmPrincipals == null || realmPrincipals.isEmpty()) {
            return null;
        }
        Collection<Set> values = realmPrincipals.values();
        for (Set set : values) {
            for (Object o : set) {
                if (type.isAssignableFrom(o.getClass())) {
                    return (T) o;
                }
            }
        }
        return null;
    }

    public <T> Collection<T> byType(Class<T> type) {
        if (realmPrincipals == null || realmPrincipals.isEmpty()) {
            return Collections.EMPTY_SET;
        }
        Set<T> typed = new LinkedHashSet<T>();
        Collection<Set> values = realmPrincipals.values();
        for (Set set : values) {
            for (Object o : set) {
                if (type.isAssignableFrom(o.getClass())) {
                    typed.add((T) o);
                }
            }
        }
        if (typed.isEmpty()) {
            return Collections.EMPTY_SET;
        }
        return Collections.unmodifiableSet(typed);
    }

    public List asList() {
        Set all = asSet();
        if (all.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return Collections.unmodifiableList(new ArrayList(all));
    }

    public Set asSet() {
        if (realmPrincipals == null || realmPrincipals.isEmpty()) {
            return Collections.EMPTY_SET;
        }
        Set aggregated = new LinkedHashSet();
        Collection<Set> values = realmPrincipals.values();
        for (Set set : values) {
            aggregated.addAll(set);
        }
        if (aggregated.isEmpty()) {
            return Collections.EMPTY_SET;
        }
        return Collections.unmodifiableSet(aggregated);
    }

    public Collection fromRealm(String realmName) {
        if (realmPrincipals == null || realmPrincipals.isEmpty()) {
            return Collections.EMPTY_SET;
        }
        Set principals = realmPrincipals.get(realmName);
        if (principals == null || principals.isEmpty()) {
            principals = Collections.EMPTY_SET;
        }
        return Collections.unmodifiableSet(principals);
    }

    public Set<String> getRealmNames() {
        if (realmPrincipals == null) {
            return null;
        } else {
            return realmPrincipals.keySet();
        }
    }

    public boolean isEmpty() {
        return realmPrincipals == null || realmPrincipals.isEmpty();
    }

    public void clear() {
        this.cachedToString = null;
        if (realmPrincipals != null) {
            realmPrincipals.clear();
            realmPrincipals = null;
        }
    }

    public Iterator iterator() {
        return asSet().iterator();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else {
            if (o instanceof SimplePrincipalCollection) {
                SimplePrincipalCollection other = (SimplePrincipalCollection) o;
                return this.realmPrincipals != null ? this.realmPrincipals.equals(other.realmPrincipals) : other.realmPrincipals == null;
            }
        }
        return false;
    }

    public int hashCode() {
        if (this.realmPrincipals != null && !this.realmPrincipals.isEmpty()) {
            return this.realmPrincipals.hashCode();
        }
        return super.hashCode();
    }

    public String toString() {
        if (this.cachedToString == null) {
            Set<Object> principals = asSet();
            if (!CollectionUtils.isEmpty(principals)) {
                this.cachedToString = StringUtils.toString(principals.toArray());
            } else {
                this.cachedToString = "empty";
            }
        }
        return this.cachedToString;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        boolean principalsExist = !CollectionUtils.isEmpty(realmPrincipals);
        out.writeBoolean(principalsExist);
        if (principalsExist) {
            out.writeObject(realmPrincipals);
        }
    }

    private void readObject(ObjectInputStream input) throws IOException, ClassNotFoundException {
        input.defaultReadObject();
        boolean principalsExist = input.readBoolean();
        if (principalsExist) {
            this.realmPrincipals = (Map<String, Set>) input.readObject();
        }
    }
}