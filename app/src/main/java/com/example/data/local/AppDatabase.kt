package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AuditLog
import com.example.data.model.CashReconciliation
import com.example.data.model.DailyClosing
import com.example.data.model.ExpenseTransaction
import com.example.data.model.FestivalEvent
import com.example.data.model.IncomeTransaction
import com.example.data.model.MandalSettings
import com.example.data.model.PendingVargani
import com.example.data.model.VarganiTransaction

@Database(
    entities = [
        VarganiTransaction::class,
        ExpenseTransaction::class,
        IncomeTransaction::class,
        PendingVargani::class,
        MandalSettings::class,
        DailyClosing::class,
        AuditLog::class,
        CashReconciliation::class,
        FestivalEvent::class
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mandalDao(): MandalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add personType and ownerName columns safely to vargani_transactions
                try {
                    db.execSQL("ALTER TABLE vargani_transactions ADD COLUMN personType TEXT NOT NULL DEFAULT 'मालक'")
                } catch (_: Exception) {}
                try {
                    db.execSQL("ALTER TABLE vargani_transactions ADD COLUMN ownerName TEXT NOT NULL DEFAULT ''")
                } catch (_: Exception) {}

                // Add personType and ownerName columns safely to pending_vargani
                try {
                    db.execSQL("ALTER TABLE pending_vargani ADD COLUMN personType TEXT NOT NULL DEFAULT 'मालक'")
                } catch (_: Exception) {}
                try {
                    db.execSQL("ALTER TABLE pending_vargani ADD COLUMN ownerName TEXT NOT NULL DEFAULT ''")
                } catch (_: Exception) {}
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    db.execSQL("ALTER TABLE vargani_transactions ADD COLUMN isCancelled INTEGER NOT NULL DEFAULT 0")
                } catch (_: Exception) {}
                try {
                    db.execSQL("ALTER TABLE vargani_transactions ADD COLUMN cancelledReason TEXT NOT NULL DEFAULT ''")
                } catch (_: Exception) {}

                try {
                    db.execSQL("ALTER TABLE expense_transactions ADD COLUMN billReceiptNumber TEXT NOT NULL DEFAULT ''")
                } catch (_: Exception) {}
                try {
                    db.execSQL("ALTER TABLE expense_transactions ADD COLUMN billImagePath TEXT NOT NULL DEFAULT ''")
                } catch (_: Exception) {}
                try {
                    db.execSQL("ALTER TABLE expense_transactions ADD COLUMN isCancelled INTEGER NOT NULL DEFAULT 0")
                } catch (_: Exception) {}
                try {
                    db.execSQL("ALTER TABLE expense_transactions ADD COLUMN cancelledReason TEXT NOT NULL DEFAULT ''")
                } catch (_: Exception) {}

                try {
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS `daily_closings` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `dateString` TEXT NOT NULL, `closingTimestamp` INTEGER NOT NULL, `totalIncome` REAL NOT NULL, `totalExpenses` REAL NOT NULL, `cashTotal` REAL NOT NULL, `upiTotal` REAL NOT NULL, `closingBalance` REAL NOT NULL, `totalPavtisCount` INTEGER NOT NULL, `totalExpensesCount` INTEGER NOT NULL, `closedBy` TEXT NOT NULL, `isClosed` INTEGER NOT NULL, `notes` TEXT NOT NULL)"
                    )
                } catch (_: Exception) {}

                try {
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS `audit_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `action` TEXT NOT NULL, `recordType` TEXT NOT NULL, `recordIdentifier` TEXT NOT NULL, `oldValue` TEXT NOT NULL, `newValue` TEXT NOT NULL, `performedBy` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `details` TEXT NOT NULL)"
                    )
                } catch (_: Exception) {}

                try {
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS `cash_reconciliations` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `dateString` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `systemCash` REAL NOT NULL, `physicalCash` REAL NOT NULL, `difference` REAL NOT NULL, `systemUpi` REAL NOT NULL, `notes` TEXT NOT NULL, `status` TEXT NOT NULL, `performedBy` TEXT NOT NULL)"
                    )
                } catch (_: Exception) {}
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    db.execSQL("ALTER TABLE expense_transactions ADD COLUMN expenseType TEXT NOT NULL DEFAULT 'REGULAR'")
                } catch (_: Exception) {}
                try {
                    db.execSQL("ALTER TABLE expense_transactions ADD COLUMN linkedAdvanceId INTEGER")
                } catch (_: Exception) {}
                try {
                    db.execSQL("ALTER TABLE expense_transactions ADD COLUMN advancePaidAmount REAL NOT NULL DEFAULT 0.0")
                } catch (_: Exception) {}
                try {
                    db.execSQL("ALTER TABLE expense_transactions ADD COLUMN totalEstimatedCost REAL NOT NULL DEFAULT 0.0")
                } catch (_: Exception) {}
                try {
                    db.execSQL("ALTER TABLE expense_transactions ADD COLUMN isSettled INTEGER NOT NULL DEFAULT 0")
                } catch (_: Exception) {}
                try {
                    db.execSQL("ALTER TABLE expense_transactions ADD COLUMN isFree INTEGER NOT NULL DEFAULT 0")
                } catch (_: Exception) {}
                try {
                    db.execSQL("ALTER TABLE expense_transactions ADD COLUMN sponsorName TEXT NOT NULL DEFAULT ''")
                } catch (_: Exception) {}
                try {
                    db.execSQL("ALTER TABLE expense_transactions ADD COLUMN isMahaprasad INTEGER NOT NULL DEFAULT 0")
                } catch (_: Exception) {}
                try {
                    db.execSQL("ALTER TABLE expense_transactions ADD COLUMN memberAttribution TEXT NOT NULL DEFAULT ''")
                } catch (_: Exception) {}

                try {
                    db.execSQL("ALTER TABLE mandal_settings ADD COLUMN executiveMembers TEXT NOT NULL DEFAULT '1. अध्यक्ष - सचिन सपकाळ\n2. उपाध्यक्ष - राहुल कदम\n3. खजिनदार - सागर शितोळे\n4. सह-खजिनदार - अमोल जगताप\n5. कार्यवाह - विकास मोरे\n6. सह-कार्यवाह - विशाल कांबळे\n7. मुख्य सल्लागार - बाबासाहेब माने\n8. सजावट प्रमुख - रोहन शिंदे\n9. महाप्रसाद प्रमुख - दीपक पाटील\n10. ध्वनी व लाईट प्रमुख - नितीन थोरात\n11. मिरवणूक प्रमुख - गणेश गायकवाड\n12. प्रसिद्धी प्रमुख - स्वप्नील भोसले\n13. सुरक्षा प्रमुख - किरण चव्हाण\n14. सदस्य - महेश सावंत\n15. सदस्य - प्रशांत पवार'")
                } catch (_: Exception) {}
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add otherPersonType and customCategoryName to vargani_transactions
                try {
                    db.execSQL("ALTER TABLE vargani_transactions ADD COLUMN otherPersonType TEXT NOT NULL DEFAULT ''")
                } catch (_: Exception) {}
                try {
                    db.execSQL("ALTER TABLE vargani_transactions ADD COLUMN customCategoryName TEXT NOT NULL DEFAULT ''")
                } catch (_: Exception) {}
                try {
                    db.execSQL("UPDATE vargani_transactions SET personType = 'घरमालक' WHERE personType = 'मालक' OR personType = 'OWNER'")
                } catch (_: Exception) {}

                // Add otherPersonType and customCategoryName to pending_vargani
                try {
                    db.execSQL("ALTER TABLE pending_vargani ADD COLUMN otherPersonType TEXT NOT NULL DEFAULT ''")
                } catch (_: Exception) {}
                try {
                    db.execSQL("ALTER TABLE pending_vargani ADD COLUMN customCategoryName TEXT NOT NULL DEFAULT ''")
                } catch (_: Exception) {}
                try {
                    db.execSQL("UPDATE pending_vargani SET personType = 'घरमालक' WHERE personType = 'मालक' OR personType = 'OWNER'")
                } catch (_: Exception) {}
            }
        }

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    db.execSQL("ALTER TABLE mandal_settings ADD COLUMN festivalName TEXT NOT NULL DEFAULT 'गणेशोत्सव २०२६'")
                } catch (_: Exception) {}
                try {
                    db.execSQL("ALTER TABLE mandal_settings ADD COLUMN festivalStartDate TEXT NOT NULL DEFAULT '2026-09-14'")
                } catch (_: Exception) {}
                try {
                    db.execSQL("ALTER TABLE mandal_settings ADD COLUMN festivalEndDate TEXT NOT NULL DEFAULT '2026-09-24'")
                } catch (_: Exception) {}
                try {
                    db.execSQL("ALTER TABLE mandal_settings ADD COLUMN accountingStartDate TEXT NOT NULL DEFAULT '2026-09-12'")
                } catch (_: Exception) {}

                try {
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS `festival_events` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`dateString` TEXT NOT NULL, " +
                            "`eventName` TEXT NOT NULL, " +
                            "`eventTime` TEXT NOT NULL, " +
                            "`programType` TEXT NOT NULL, " +
                            "`customProgramType` TEXT NOT NULL, " +
                            "`location` TEXT NOT NULL, " +
                            "`responsibleMember` TEXT NOT NULL, " +
                            "`responsibleMobile` TEXT NOT NULL, " +
                            "`aartiContributorName` TEXT NOT NULL, " +
                            "`aartiContributorType` TEXT NOT NULL, " +
                            "`aartiTime` TEXT NOT NULL, " +
                            "`mahaprasadContributorName` TEXT NOT NULL, " +
                            "`mahaprasadContributorType` TEXT NOT NULL, " +
                            "`flowerArrangementType` TEXT NOT NULL, " +
                            "`flowerContributorName` TEXT NOT NULL, " +
                            "`flowerEstimatedCost` REAL NOT NULL, " +
                            "`flowerActualExpense` REAL NOT NULL, " +
                            "`otherArrangements` TEXT NOT NULL, " +
                            "`status` TEXT NOT NULL, " +
                            "`notes` TEXT NOT NULL, " +
                            "`estimatedBudget` REAL NOT NULL, " +
                            "`optionalFinancialAmount` REAL NOT NULL, " +
                            "`isLinkedToFinancials` INTEGER NOT NULL, " +
                            "`createdTimestamp` INTEGER NOT NULL" +
                            ")"
                    )
                } catch (_: Exception) {}
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "akgmm_mandal_db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
                    .fallbackToDestructiveMigrationOnDowngrade()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
