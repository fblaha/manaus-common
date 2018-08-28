package cz.fb.manaus.ischia.filter

import com.google.common.collect.Range
import cz.fb.manaus.reactor.betting.listener.FlowFilter
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile(ManausProfiles.PRODUCTION)
class DrawFlowFilter : FlowFilter(Range.all(), Range.singleton(1),
        { _, runner -> runner.name.toLowerCase().contains("draw") }, emptySet())
