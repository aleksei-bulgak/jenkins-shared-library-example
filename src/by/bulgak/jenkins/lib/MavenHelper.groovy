package by.bulgak.jenkins.lib;

import static Constants.MAVEN;

def mvn1(String action, String settings = null) {
    try {
        String command = "${tool '${MAVEN}'}/bin/mvn ${action}";
        if (settings != null) {
            command += " -gs ${settings}";
        }
        sh command;
    } catch (err) {
        print err
    }
}

class MavenHelper implements Serializable {
    def steps
    
    MavenHelper(steps) {this.steps = steps}

    def mvn(args) {
        steps.sh "${steps.tool '${MAVEN}'}/bin/mvn ${action}"
    }
}