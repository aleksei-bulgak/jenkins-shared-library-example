package by.bulgak.jenkins.lib;

import static Constants.MAVEN;

class MavenHelper implements Serializable {
    def steps
    
    MavenHelper(steps) {this.steps = steps}

    def mvn(String action, String mavenVersion = MAVEN) {
        try{
            def mavenPath = steps.tool "${mavenVersion}";
            return steps.sh("${mavenPath}/bin/mvn ${action}")
        }catch(Exception ex){
            print ex.getMessage();
        }
    }
}