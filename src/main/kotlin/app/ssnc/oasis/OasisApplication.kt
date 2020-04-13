package app.ssnc.oasis

import app.ssnc.oasis.constant.Constants
import app.ssnc.oasis.util.DefaultProfileUtil
import mu.KotlinLogging
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import java.net.InetAddress
import javax.annotation.PostConstruct

private val log = KotlinLogging.logger{ }

@SpringBootApplication
open class OasisApplication() {

	@Bean
	open fun init(environment: Environment) = CommandLineRunner {
		val activeProfiles: Array<String> = environment.activeProfiles
		if (activeProfiles.contains(Constants.SPRING_PROFILE_DEVELOPMENT)
				&& activeProfiles.contains(Constants.SPRING_PROFILE_PRODUCTION)) {
			log.error {
				"""
                    You have misconfigured your application!
                    It should not run with both the '${Constants.SPRING_PROFILE_DEVELOPMENT}'
                    and ${Constants.SPRING_PROFILE_PRODUCTION} profiles at the same time
                """
			}
		}
		if (activeProfiles.contains(Constants.SPRING_PROFILE_DEVELOPMENT)
				&& activeProfiles.contains(Constants.SPRING_PROFILE_CLOUD)) {
			log.error {
				"""
                    You have misconfigured your application!
                    It should not run with both the '${Constants.SPRING_PROFILE_DEVELOPMENT}'
                    and ${Constants.SPRING_PROFILE_CLOUD} profiles at the same time
                """
			}
		}
	}
	companion object {
		@JvmStatic
		fun main(args: Array<String>) {
			val initialApp = SpringApplication(OasisApplication::class.java)
			val app: SpringApplication = DefaultProfileUtil.addDefaultProfile(initialApp)
			val environment: ConfigurableEnvironment = app.run(*args).environment
			val hasServerSslKeystore: Boolean = !environment.getProperty(Constants.SERVER_SSL_KEYSTORE).isNullOrEmpty()
			val protocol: String = if (hasServerSslKeystore) Constants.HTTPS else Constants.HTTP
			log.debug {
				"""
            -------------------------------------------------------------------------
            1)Application: '${environment[Constants.SPRING_APPLICATION_NAME]}' is running!
            2)Access URLs:
            2.1)Local: $protocol://localhost:${environment[Constants.SERVER_PORT]}
            2.2)External: $protocol://${InetAddress.getLocalHost().hostAddress}:${environment[Constants.SERVER_PORT]}
            3)Profiles: ${environment.activeProfiles.contentToString()}
            -------------------------------------------------------------------------
        """
			}
		}
	}
}

//fun main(args: Array<String>) {
//	val initialApp = SpringApplication(OasisApplication::class.java)
//	val app: SpringApplication = DefaultProfileUtil.addDefaultProfile(initialApp)
//	val environment: ConfigurableEnvironment = app.run(*args).environment
//	val hasServerSslKeystore: Boolean = !environment.getProperty(Constants.SERVER_SSL_KEYSTORE).isNullOrEmpty()
//	val protocol: String = if (hasServerSslKeystore) Constants.HTTPS else Constants.HTTP
//	log.debug {
//		"""
//            -------------------------------------------------------------------------
//            1)Application: '${environment[Constants.SPRING_APPLICATION_NAME]}' is running!
//            2)Access URLs:
//            2.1)Local: $protocol://localhost:${environment[Constants.SERVER_PORT]}
//            2.2)External: $protocol://${InetAddress.getLocalHost().hostAddress}:${environment[Constants.SERVER_PORT]}
//            3)Profiles: ${environment.activeProfiles.contentToString()}
//            -------------------------------------------------------------------------
//        """
//	}
//}
//@SpringBootApplication
//@EnableAutoConfiguration(exclude = [(RepositoryRestMvcAutoConfiguration::class)])
//class OasisApplication
//
//fun main(args: Array<String>) {
//	runApplication<OasisApplication>(*args)
//}