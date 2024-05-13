package guestbook

import com.zaxxer.hikari.HikariDataSource
import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.*
import org.springframework.web.filter.CommonsRequestLoggingFilter

@Configuration
@EnableAutoConfiguration(exclude = [DataSourceAutoConfiguration::class])
@ComponentScan
@ConfigurationPropertiesScan
class GuestBookApplication {
  @Bean
  @Primary
  fun dataSourceProperties(): DataSourceProperties {
    return DataSourceProperties()
  }

  @Bean
  @Profile("postgres")
  fun dataSource(properties: DataSourceProperties): HikariDataSource {
    val pool = properties
      .initializeDataSourceBuilder()
      .type(HikariDataSource::class.java)
      .build()

    LoggerFactory.getLogger(javaClass).warn("Created Hikari pool: $pool")
    LoggerFactory.getLogger(javaClass).warn("Hikari database: ${properties.determineDatabaseName()}")
    LoggerFactory.getLogger(javaClass).warn("Hikari url: ${properties.determineUrl()}")
    LoggerFactory.getLogger(javaClass).warn("Hikari user: ${properties.determineUsername()}")

    return pool
  }

  @Bean
  fun logFilter() = CommonsRequestLoggingFilter().apply {
    setIncludeQueryString(true)
    setIncludePayload(true)
    setMaxPayloadLength(1000)
    setIncludeHeaders(true)
    setAfterMessagePrefix("REQUEST DATA : ")
  }

  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      SpringApplication.run(GuestBookApplication::class.java, *args)
    }
  }
}