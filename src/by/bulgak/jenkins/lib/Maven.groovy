package by.bulgak.jenkins.lib;

import com.cloudbees.groovy.cps.NonCPS

class Maven implements Serializable {
    boolean silent;
    def arguments = new ArrayList<MavenArgument>();
    def action;
    def steps;

    private Maven(steps, arguments, action, silent){
        this.steps = steps
        this.arguments = arguments
        this.action = action
        this.silent = silent
    }

    def execute(String mvnVersion){
        println "TEST"
        try{
            def maven = steps.tool "${mvnVersion}"
            println "${maven}/bin/mvn ${action} ${resolveArgs()}"
            return steps.sh("${maven}/bin/mvn ${action} ${resolveArgs()}");
        } catch(err){
            print err
            check(err)
        }
        check()
    }

    @NonCPS
    private String resolveArgs(){
        String result = "";
        arguments.each{argument -> result += argument.toString()}
        println result;
        return result
    }

    @NonCPS
    private void check(def ex = null) {
        if (!silent) {
            if (steps.currentBuild.result == 'UNSTABLE')
                steps.error "Stage is failed"
            if (ex != null)
                throw ex
        } else {
            if (ex != null)
                steps.currentBuild.result = 'UNSTABLE'
        }
    }

    String toString() {
        return "Maven{" +
                "silent=" + silent +
                ", arguments=" + arguments +
                ", action=" + action +
                ", steps=" + steps +
                '}';
    }

    @NonCPS
    static def builder(steps){
        return new Maven.Builder(steps);
    }

    static class Builder implements Serializable{
        boolean silent;
        def arguments = new ArrayList<MavenArgument>();
        def action;
        def steps;
        
        Builder(steps){
            this.steps = steps
        }

        @NonCPS
        def action(String action){
            this.action = action
            return this
        }

        @NonCPS
        def steps(steps){
            this.steps = steps
            return steps
        }

        @NonCPS
        def argument(MavenArgument arg){
            this.arguments << arg
            return this
        }

        @NonCPS
        def silent(boolean silent){
            this.silent = silent
            return this
        }

        @NonCPS
        def build(){
            Maven result = new Maven(steps, arguments, action, silent);
            return result
        }
    }
}

/*
Maven mvn = new Maven([:]);
mvn.action("action test");
mvn.appendArgument("key", "value");
mvn.appendArgument("key2", "value2");
println mvn.execute("version");
*/