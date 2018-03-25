### This repo was created to test Jenkins Shared Library feature.

Also this repository was used to present Shared libraries at work to my colegues

1. First ste was to show that before Jenkins 2 developers used to create Jenkins jobs manually via UI.

2. With release of Jenkins 2 developers provide ability to create different pipelines with the help of pipeline dsl.

3. Pipelines  is really cool feature but in most cases we develop/support many small application and each applications need its own pipeline.

4. As a result Shared libraries appeared.

#### To create this repository I use official [jenkins shared library documentation](https://jenkins.io/doc/book/pipeline/shared-libraries/)

As first example I created hello world application that just send print text that was inserted into helloWorld method:

```
// Jenkinsfile
node {
	library (
		identifier: 'first-lib@first-step', // unique name of your shared lib and branch/tag specifier 
		retriever: 
			modernSCM([
                // ddownload shared library function
			])
	)
	helloWorld "Test message"
}


// var/helloWorld.groovy
def call(String textMessage){
	print textMessage
}
```

### Same example via class and function out of class scope
```
// Jenkinsfile
node{

	def lib = library (
		identifier: 'first-lib@first-step', // unique name of your shared lib and branch/tag specifier 
		retriever: 
			modernSCM([
                // ddownload shared library function
			])
	)
	helloWorld "Hello from library function"
	
    library ("static-shared-lib@lib").by.bulgak.jenkins.lib.HelloWorld.new(this).say("Hello frm class")

    helloWorld "Hello from library function"
    lib.by.bulgak.jenkins.lib.HelloWorld.new(this).mvn("Hello frm class")
    lib.by.bulgak.jenkins.lib.HelloWorldStatic.new().mvn("Hello frm class")
}

// MavenHelperStatic.groovy
import static Constants.MAVEN;

def mvn(String action, String mavenVersion = MAVEN) {
    def mavenPath = tool "${mavenVersion}";
    sh "${mavenPath}/bin/mvn ${action}"
}

// MavenHelper.groovy
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
```

### So lets take a look at simple maven application that is build, tested and pushed into local maven repo with different approaches

1. Simple jenkins pipeline

```
    node {
        stage("Checkout"){
            checkout([
                $class: 'GitSCM', 
                branches: [[name: 'refs/tags/general']], 
                doGenerateSubmoduleConfigurations: false, 
                userRemoteConfigs: [[
                    credentialsId: 'CREDENTIALS', 
                    url: 'repo with source code'
                ]]
            ])
        }
        stage("Compile"){
            sh("${tool 'Apache Maven 3.3.9'}/bin/mvn compile")
        }
        stage("Test"){
            sh("${tool 'Apache Maven 3.3.9'}/bin/mvn test")
        }
        stage("Install"){
            sh("${tool 'Apache Maven 3.3.9'}/bin/mvn install")
        }
    }
```

Here we just download repository with source code and move code through three stages: compie, test, install. This is common example and of couse we do not need any libraries for this case. But for case of study this example can be rewriten via `MavenHelperStatic` or `MavenHelper`

2. Simple pipeline with some helper classes
```
    /**
    * To use code from shared library we need to initalize it
    * in this example we use dynamic funtion to download shared library
    * also you can use @Library static function
    */
	def lib = library (
		identifier: 'simple-lib@lib', 
		retriever: 
			modernSCM([
				$class: 'GitSCMSource', 
				credentialsId: '92bb99b8-238f-4f06-8386-b45861c9dd76', 
				id: '42de5809-9017-4717-90f7-1e55cd53e5f7', 
				remote: 'git@github.com:aleksei-bulgak/jenkins-shared-library-example.git', 
				traits: [[$class: 'jenkins.plugins.git.traits.BranchDiscoveryTrait']]
			])
	)

    /**
    * we do not want to use complex checkout that is provided via pipeline 
    * sintax so we created simle Checkout class that accept two arguments
    * firt   - repository where source code is stored
    * second - optional parameter if you want to specify non master branch
    */ 
    lib.by.bulgak.jenkins.lib.Checkout.new().checkout(
        "repo with source code",
        "branch/tag" //master will be used by default
    );

    /**
    * Here we initialize MavenHelperStatic funtion.
    * of couse we could mode this code into `var/` folder and in this case 
    * initialization will be not needed 
    */
    def mvnStatic = lib.by.bulgak.jenkins.lib.MavenHelperStatic.new();
    stage("Compile"){
        //sh("${tool 'Apache Maven 3.3.9'}/bin/mvn compile")
        mvnStatic.mvn("compile");
    }
    stage("Test"){
        //sh("${tool 'Apache Maven 3.3.9'}/bin/mvn test")
        lib.by.bulgak.jenkins.lib.MavenHelperStatic.new().mvn("test");
    }
    stage("Install"){
        //sh("${tool 'Apache Maven 3.3.9'}/bin/mvn install")
        def maven = lib.by.bulgak.jenkins.lib.MavenHelper.new(this);
        maven.mvn("install");
    }
```

3. Same code with the help of var/mavenStep.groovy
```
node {
	def lib = library (
		identifier: 'first-lib@first-step', // unique name of your shared lib and branch/tag specifier 
		retriever: 
			modernSCM([
                // ddownload shared library function
			])
	)

    lib.by.bulgak.jenkins.lib.Checkout.new().checkout(
        "repo with source code",
        "branch/tag" //master will be used by default
    );

    mavenStep{
        name = "Compile"
        action = "compile"
    }


    mavenStep{
        name = "Run Tests"
        action = "test"
    }
    
    mavenStep{
        name = "Build code and put into local repository"
        action = "install"
    }
}
```
4. More complex code with builder classes
```
node {

	def lib = library (
		identifier: 'first-lib@first-step', // unique name of your shared lib and branch/tag specifier 
		retriever: 
			modernSCM([
                // ddownload shared library function
			])
	)
    stage("Checkout class"){
        lib.by.bulgak.jenkins.lib.Checkout.new().checkout(
            "repo with source code",
            "branch/tag" //master will be used by default
        );
    }

    stage("Maven builder"){
        def mavenLib = lib.by.bulgak.jenkins.lib
        def mvn = mavenLib.Maven.builder(this)
            .action("clean install")
            .argument(mavenLib.MavenArgument.create().withPrefix("-D").withKey("key").withName("value").build())
            .argument(mavenLib.MavenArgument.create().withPrefix("-D").withKey("key2").withName("value2").build())
            .silent(true)
            .build();
       def constants = mavenLib.Constants.new()
        mvn.execute(constants.MAVEN);
    }
}
```