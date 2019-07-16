package cz.fb.manaus.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.net.HttpHeaders
import cz.fb.manaus.core.model.MarketFootprint
import cz.fb.manaus.core.model.market
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import kotlin.test.assertNotNull


class MarketFootprintControllerTest : AbstractControllerTest() {
    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `get by ID`() {
        createLiveMarket()
        checkResponse("/footprints/2", "Banik", "Sparta")
    }

    @Test
    fun import() {
        val footprint = MarketFootprint(market, emptyList(), emptyList())
        val market = objectMapper.writer().writeValueAsString(footprint)
        val result = mvc.perform(MockMvcRequestBuilders.post("/footprints")
                .content(market)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andReturn()
        assertNotNull(result.response.getHeader(HttpHeaders.LOCATION))
    }
}