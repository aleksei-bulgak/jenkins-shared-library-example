package by.bulgak.jenkins.lib;

import static Constants.MAVEN;

def mvn(String action, String mavenVersion = MAVEN) {
       def command = "${tool 'Apache Maven 3.3.9'}/bin/mvn ${action}";
       return sh(command);
}