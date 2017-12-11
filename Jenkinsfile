node {

	def lib = library (
		identifier: 'simple-lib@temp', 
		retriever: 
			modernSCM([
				$class: 'GitSCMSource', 
				credentialsId: '92bb99b8-238f-4f06-8386-b45861c9dd76', 
				id: '42de5809-9017-4717-90f7-1e55cd53e5f7', 
				remote: 'git@github.com:aleksei-bulgak/jenkins-shared-library-example.git', 
				traits: [[$class: 'jenkins.plugins.git.traits.BranchDiscoveryTrait']]
			])
	)
    stage("Checkout class"){
        lib.by.bulgak.jenkins.lib.Checkout.new().checkout(
            "git@github.com:aleksei-bulgak/jenkins-shared-library-example.git",
            "master"
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