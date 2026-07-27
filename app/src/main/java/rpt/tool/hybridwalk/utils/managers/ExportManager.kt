package rpt.tool.hybridwalk.utils.managers

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import rpt.tool.hybridwalk.utils.data.appmodels.DailyRecord
import java.time.LocalDate
import androidx.core.graphics.toColorInt

object ExportManager {

    fun writeCsvToUri(context: Context, uri: Uri, records: List<DailyRecord>) {
        context.contentResolver.openOutputStream(uri)?.bufferedWriter()?.use { writer ->
            writer.write("Data,Passi,Obiettivo,Smart Working,Giorno Scarico (Palestra)\n")

            records.sortedBy { it.dateEpochDay }.forEach { record ->
                val dateStr = LocalDate.ofEpochDay(record.dateEpochDay).toString()
                val wfh = if (record.isWfhDay) "Si" else "No"
                val gym = if (record.isGymDay) "Si" else "No"
                writer.write("$dateStr,${record.stepCount},${record.stepGoal},$wfh,$gym\n")
            }
        }
    }

    fun writePdfToUri(context: Context, uri: Uri, records: List<DailyRecord>) {
        val pdfDocument = PdfDocument()
        val paint = Paint()

        var pageInfo = PdfDocument.PageInfo.Builder(595, 842,
            1).create() 
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas
        var yPos = 50f

        paint.textSize = 24f
        paint.isFakeBoldText = true
        paint.color = "#81B29A".toColorInt() 
        canvas.drawText("Report Salute - HybridWalk", 50f, yPos, paint)
        yPos += 40f

        paint.textSize = 14f
        paint.color = android.graphics.Color.BLACK
        canvas.drawText("Data", 50f, yPos, paint)
        canvas.drawText("Passi", 200f, yPos, paint)
        canvas.drawText("Smart Working", 320f, yPos, paint)
        canvas.drawText("Palestra", 470f, yPos, paint)
        yPos += 20f

        paint.isFakeBoldText = false

        records.sortedBy { it.dateEpochDay }.forEach { record ->
            if (yPos > 800f) {
                pdfDocument.finishPage(page)
                pageInfo = PdfDocument.PageInfo.Builder(595, 842,
                    pdfDocument.pages.size + 1).create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                yPos = 50f
            }

            val dateStr = LocalDate.ofEpochDay(record.dateEpochDay).toString()
            canvas.drawText(dateStr, 50f, yPos, paint)
            canvas.drawText("${record.stepCount} / ${record.stepGoal}", 200f, yPos, paint)
            canvas.drawText(if (record.isWfhDay) "Si" else "No", 320f, yPos, paint)
            canvas.drawText(if (record.isGymDay) "Si" else "No", 470f, yPos, paint)
            yPos += 20f
        }
        pdfDocument.finishPage(page)

        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            pdfDocument.writeTo(outputStream)
        }
        pdfDocument.close()
    }
}