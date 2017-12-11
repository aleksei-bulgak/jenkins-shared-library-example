package by.bulgak.jenkins.lib;

class MavenHelper implements Serializable {
    def steps
    
    MavenHelper(steps) {this.steps = steps}

    def mvn(args) {
        steps.sh "${steps.tool 'Apache Maven 3.3.9'}/bin/mvn ${args}"
    }
}