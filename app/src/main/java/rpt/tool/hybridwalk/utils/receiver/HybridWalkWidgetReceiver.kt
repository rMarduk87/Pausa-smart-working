package rpt.tool.hybridwalk.utils.receiver

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import rpt.tool.hybridwalk.utils.view.widget.HybridWalkWidget

class HybridWalkWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = HybridWalkWidget()
}