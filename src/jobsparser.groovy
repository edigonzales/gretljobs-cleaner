@GrabResolver(name='sonatype', root='https://oss.sonatype.org/content/repositories/snapshots/')
@Grab(group='com.github.jsqlparser', module='jsqlparser', version='2.0')
@Grab(group='org.eclipse.jgit', module='org.eclipse.jgit', version='5.0.2.201807311906-r')
@Grab(group='org.apache.poi', module='poi', version='3.17')
@Grab(group='org.apache.poi', module='poi-ooxml', version='3.17')

import groovy.io.FileType

import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement
import net.sf.jsqlparser.statement.Statements
import net.sf.jsqlparser.statement.delete.Delete
import net.sf.jsqlparser.statement.insert.Insert
import net.sf.jsqlparser.statement.select.Select
import net.sf.jsqlparser.util.TablesNamesFinder

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.eclipse.jgit.api.Git

def jobsRepository = "https://github.com/sogis/gretljobs"
def localCloneDirectory = "../gretljobs"

// Clone jobs repository.
// Existing directory will be deleted.
new File(localCloneDirectory).deleteDir()
Git git = Git.cloneRepository()
        .setURI(jobsRepository)
        .setDirectory(new File(localCloneDirectory))
        .call()

// At the moment some of the sql queries cannot
// be parsed with jsqlparser.
def sqlFilesBlacklist = []
// 2018-09-02
sqlFilesBlacklist.add("/arp_naturreservate_pub/arp_naturreservate_pub_naturreservate_teilgebiete.sql")
sqlFilesBlacklist.add("/arp_naturreservate_pub/arp_naturreservate_pub_naturreservate_reservate.sql")
sqlFilesBlacklist.add("/agi_av_kaso_abgleich_pub/agi_av_kaso_abgleich_import_differenzen_staging.sql")
sqlFilesBlacklist.add("/agi_av_kaso_abgleich_pub/agi_av_kaso_abgleich_import_uebersicht_des_vergleichs_staging.sql")
sqlFilesBlacklist.add("/afu_isboden_pub/afu_isboden_pub_bodenprofilstandort.sql")
sqlFilesBlacklist.add("/amb_zivilschutz_adressen_export/amb_zivilschutz_adressen_staging_adressen_zivilschutz.sql")
sqlFilesBlacklist.add("/arp_aggloprogramme_pub/arp_aggloprogramme_pub_agglomrtnsprgrmme_massnahme.sql")
sqlFilesBlacklist.add("/afu_gewschutz_export_ai/export/gsareal_insert.sql")
sqlFilesBlacklist.add("/afu_gewschutz_export_ai/export/gsbereich_insert.sql")
sqlFilesBlacklist.add("/afu_gewschutz_export_ai/export/gszone_insert.sql")
sqlFilesBlacklist.add("/agi_av_gb_abgleich_pub/agi_av_gb_abgleich_import_uebersicht_des_vergleichs_staging.sql")
sqlFilesBlacklist.add("/agi_av_gb_abgleich_pub/agi_av_gb_abgleich_import_differenzen_staging.sql")

// 2019-04-26
sqlFilesBlacklist.add("/arp_nutzungsvereinbarung_pub/arp_nutzungsvereinbarung_pub_nutzungsvrnbrngen_nutzungsvreinbarungen.sql")
sqlFilesBlacklist.add("/arp_nutzungsvereinbarung_pub_sogis/arp_nutzungsvereinbarung_pub_nutzungsvrnbrngen_nutzungsvereinbarungen.sql")
sqlFilesBlacklist.add("/avt_oevkov/berechnung_kosten.sql")
sqlFilesBlacklist.add("/avt_oevkov/insert_into_gtfs_auswertung.sql")
sqlFilesBlacklist.add("/avt_oevkov/haltestellenbuffer_insert.sql")
sqlFilesBlacklist.add("/avt_oevkov/insert_into_gesamtauswertung.sql")
sqlFilesBlacklist.add("/afu_wasserbewirtschaftung_pub/afu_wasserbewirtschaftung_grundwassereinbauten_pub.sql")
sqlFilesBlacklist.add("/afu_isboden_pub/afu_isboden_pub_bodeneinheit.sql")
sqlFilesBlacklist.add("/arp_richtplan_pub/arp_richtplan_pub_richtplankarte_grundnutzung_sogis.sql")
sqlFilesBlacklist.add("/arp_richtplan_pub/arp_richtplan_pub_richtplankarte_ueberlagernde_linie_sogis.sql")
sqlFilesBlacklist.add("/arp_richtplan_pub/arp_richtplan_pub_richtplankarte_ueberlagernde_flaeche_sogis.sql")
sqlFilesBlacklist.add("/arp_richtplan_pub/arp_richtplan_pub_richtplankarte_grundnutzung.sql")
sqlFilesBlacklist.add("/arp_richtplan_pub/arp_richtplan_pub_richtplankarte_ueberlagernde_flaeche.sql")
sqlFilesBlacklist.add("/arp_richtplan_pub/arp_richtplan_pub_richtplankarte_ueberlagernde_linie.sql")
sqlFilesBlacklist.add("/arp_richtplan_pub/arp_richtplan_pub_richtplankarte_ueberlagernder_punkt.sql")
sqlFilesBlacklist.add("/arp_aggloprogramme_pub_sogis/arp_aggloprogramme_pub_agglomrtnsprgrmme_massnahme.sql")
sqlFilesBlacklist.add("/agi_hoheitsgrenzen_pub/agi_hoheitsgrenzen_pub_sogis_hoheitsgrenzen_gemeindegrenze.sql")
sqlFilesBlacklist.add("/agi_hoheitsgrenzen_pub/agi_hoheitsgrenzen_pub_sogis_hoheitsgrenzen_gemeindegrenze_generalisiert.sql")
sqlFilesBlacklist.add("/arp_npl_export_ai/arp_npl_mgdm_rechtsvorschrften_dokument.sql")
sqlFilesBlacklist.add("/arp_npl_export_ai/arp_laermempfindlichkeit_mgdm_rechtsvorschrften_dokument.sql")
sqlFilesBlacklist.add("/arp_npl_pub/transform_arp_npl_pub_nutzungsplanung_ueberlagernd_punkt_json_dokumente.sql")
sqlFilesBlacklist.add("/arp_npl_pub/transform_arp_npl_pub_erschliessung_flaechenobjekt_json_dokumente.sql")
sqlFilesBlacklist.add("/arp_npl_pub/transform_arp_npl_pub_nutzungsplanung_grundnutzung_json_dokumente.sql")
sqlFilesBlacklist.add("/arp_npl_pub/transform_arp_npl_pub_erschliessung_linienobjekt_json_dokumente.sql")
sqlFilesBlacklist.add("/arp_npl_pub/transform_arp_npl_pub_nutzungsplanung_ueberlagernd_flaeche_json_dokumente.sql")
sqlFilesBlacklist.add("/arp_npl_pub/transform_arp_npl_pub_nutzungsplanung_ueberlagernd_linie_json_dokumente.sql")


// Get all sql files in the clone repo.
def list = []
def dir = new File(localCloneDirectory)
dir.traverse (type: FileType.FILES, nameFilter: ~/(?i).*.sql/) { file ->
    list << file
}

// Prepare xlsx
Workbook workbook = new XSSFWorkbook()
Sheet sheet = workbook.createSheet("Tables")

// Loop through all sql files and parse the query.
def allTables = []
int rowNum = 0
list.each {
    println it.path

    if (sqlFilesBlacklist.contains(it.path.substring(localCloneDirectory.size()))) {
        println "Blacklist: " + it.path
        return
    }
    def file = new File(it.path)
    def sql = file.text

    //try {
        Statements stmts = CCJSqlParserUtil.parseStatements(sql)
        for (Statement stmt : stmts.statements) {
            TablesNamesFinder tablesNamesFinder = new TablesNamesFinder()
            List<String> tableList = (tablesNamesFinder.getTableList(stmt))
            allTables.addAll(tableList)

            tableList.each { table ->
                Row row = sheet.createRow(rowNum++);
                try {
                    // create xlsx row with sql file name and qualified table name
                    println table + "," + it.path.substring(localCloneDirectory.size())
                    row.createCell(0).setCellValue(table)
                    println table
                    def splitted = table.split("\\.")
                    if (splitted.size() == 1) {
                        row.createCell(1).setCellValue("")
                        row.createCell(2).setCellValue(splitted[0])
                    } else {
                        row.createCell(1).setCellValue(splitted[0])
                        row.createCell(2).setCellValue(splitted[1])
                    }
                    row.createCell(3).setCellValue(it.path.substring(localCloneDirectory.size()))
                } catch (java.lang.NullPointerException e) {
                    e.printStackTrace()
                }
            }
        }
    //} catch (Exception e) {
    //    e.printStackTrace()
    //}
}

// Write xlxs file.
FileOutputStream fileOut = new FileOutputStream("table_names.xlsx");
workbook.write(fileOut)
fileOut.close()
workbook.close()

println allTables