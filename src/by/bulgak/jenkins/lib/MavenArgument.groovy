package by.bulgak.jenkins.lib;

import com.cloudbees.groovy.cps.NonCPS

class MavenArgument implements Serializable{
    def prefix
    def key
    def value

    private MavenArgument(prefix){
        this.prefix = prefix
    }

    @NonCPS
    String toString(){
        println "${prefix}${key}=${value}"
        return " ${prefix}${key}=${value}"
    }

    @NonCPS
    static def create(){
        return new MavenArgument.Builder();
    }

    static class Builder implements Serializable {
        def prefix
        def key
        def value

        @NonCPS
        def withPrefix(String prefix){ 
            this.prefix = prefix
            return this
        }

        @NonCPS
        def withKey(String key){
            this.key = key
            return this
        }

        @NonCPS
        def withName(String name){
            this.value = name
            return this
        }

        @NonCPS 
        def build(){
            MavenArgument result = new MavenArgument(prefix);
            result.key = this.key
            result.value = this.value
            return result
        }
    }
}