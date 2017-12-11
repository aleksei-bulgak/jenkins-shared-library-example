package by.bulgak.jenkins.lib;

import static Constants.MAVEN;

def mvn(String action, String mavenVersion = MAVEN) {
    def mavenPath = tool "${mavenVersion}";
    sh "${mavenPath}/bin/mvn ${action}"
}