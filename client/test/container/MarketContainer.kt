package se.rbkn99.container

import org.testcontainers.containers.FixedHostPortGenericContainer
//import org.testcontainers.containers.GenericContainer

class MarketContainer(dockerImageName: String) : FixedHostPortGenericContainer<MarketContainer>(dockerImageName)