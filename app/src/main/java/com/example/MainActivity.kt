package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.model.ExpenseTransaction
import com.example.data.model.PendingVargani
import com.example.data.model.VarganiTransaction
import com.example.ui.components.AddExpenseDialog
import com.example.ui.components.AddPendingDialog
import com.example.ui.components.AddVarganiDialog
import com.example.ui.components.AiAssistantDialog
import com.example.ui.components.AppBottomNavigationBar
import com.example.ui.components.AppNavigationDrawerContent
import com.example.ui.components.AppTopBar
import com.example.ui.components.DailyClosingDialog
import com.example.ui.components.FinalReportDialog
import com.example.ui.components.PavtiDetailSheet
import com.example.ui.components.ReconciliationDialog
import com.example.ui.screens.AuditHistoryScreen
import com.example.ui.screens.CalendarScheduleScreen
import com.example.ui.screens.DailyClosingScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ExpensesScreen
import com.example.ui.screens.LockScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.PendingVarganiScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.VarganiScreen
import com.example.ui.theme.MandalAccountsTheme
import com.example.ui.theme.OrangeBackground
import com.example.ui.viewmodel.MandalViewModel
import com.example.util.ShareHelper
import com.example.util.backup.BackupManager
import com.example.util.excel.ExcelReportGenerator
import com.example.util.pdf.PdfReceiptGenerator
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: MandalViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MandalAccountsTheme {
                val context = LocalContext.current
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: "dashboard"

                // State from ViewModel
                val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
                val settings by viewModel.settings.collectAsStateWithLifecycle()
                val varganiList by viewModel.varganiList.collectAsStateWithLifecycle()
                val expenseList by viewModel.expenseList.collectAsStateWithLifecycle()
                val incomeList by viewModel.incomeList.collectAsStateWithLifecycle()
                val pendingList by viewModel.pendingList.collectAsStateWithLifecycle()
                val ownerWiseRecords by viewModel.ownerWiseRecords.collectAsStateWithLifecycle()
                val allOwnerNames by viewModel.allOwnerNames.collectAsStateWithLifecycle()
                val dailyClosings by viewModel.dailyClosings.collectAsStateWithLifecycle()
                val reconciliations by viewModel.cashReconciliations.collectAsStateWithLifecycle()
                val auditLogs by viewModel.auditLogs.collectAsStateWithLifecycle()
                val festivalEvents by viewModel.festivalEvents.collectAsStateWithLifecycle()

                val totalVargani by viewModel.totalVargani.collectAsStateWithLifecycle()
                val totalOwnerVargani by viewModel.totalOwnerVargani.collectAsStateWithLifecycle()
                val totalTenantVargani by viewModel.totalTenantVargani.collectAsStateWithLifecycle()
                val totalOtherVargani by viewModel.totalOtherVargani.collectAsStateWithLifecycle()
                val totalExpense by viewModel.totalExpense.collectAsStateWithLifecycle()
                val netBalance by viewModel.netBalance.collectAsStateWithLifecycle()
                val todayVargani by viewModel.todayVargani.collectAsStateWithLifecycle()
                val todayExpenses by viewModel.todayExpenses.collectAsStateWithLifecycle()
                val totalCashVargani by viewModel.totalCashVargani.collectAsStateWithLifecycle()
                val totalUpiVargani by viewModel.totalUpiVargani.collectAsStateWithLifecycle()
                val totalCashExpense by viewModel.totalCashExpense.collectAsStateWithLifecycle()
                val totalUpiExpense by viewModel.totalUpiExpense.collectAsStateWithLifecycle()
                val netCashInHand by viewModel.netCashInHand.collectAsStateWithLifecycle()
                val netUpiInBank by viewModel.netUpiInBank.collectAsStateWithLifecycle()
                val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

                val ownerCount by viewModel.totalOwnersCount.collectAsStateWithLifecycle()
                val tenantCount by viewModel.totalTenantsCount.collectAsStateWithLifecycle()
                val otherCount by viewModel.totalOthersCount.collectAsStateWithLifecycle()
                val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
                val unsettledAdvances by viewModel.unsettledAdvancesList.collectAsStateWithLifecycle()
                val executiveMembers by viewModel.executiveMembersList.collectAsStateWithLifecycle()

                // Lock Screen State (PIN lock on top of session)
                var isAppUnlocked by remember { mutableStateOf(!settings.isPinEnabled) }

                // Dialog States
                var showAddVarganiDialog by remember { mutableStateOf(false) }
                var editingVargani by remember { mutableStateOf<VarganiTransaction?>(null) }
                var nextPavtiNumber by remember { mutableStateOf("AKGMM-2026-00001") }

                var showAddExpenseDialog by remember { mutableStateOf(false) }
                var editingExpense by remember { mutableStateOf<ExpenseTransaction?>(null) }

                var showAddPendingDialog by remember { mutableStateOf(false) }
                var showAiAssistantDialog by remember { mutableStateOf(false) }

                // Advanced Governance Dialogs
                var showDailyClosingDialog by remember { mutableStateOf(false) }
                var showReconciliationDialog by remember { mutableStateOf(false) }
                var showFinalReportDialog by remember { mutableStateOf(false) }
                var showAuditHistoryDialog by remember { mutableStateOf(false) }

                // Pavti Detail Sheet State
                var selectedPavtiForDetail by remember { mutableStateOf<VarganiTransaction?>(null) }
                val pavtiSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                val scope = rememberCoroutineScope()

                // Splash Screen State
                var showSplash by remember { mutableStateOf(true) }

                if (showSplash) {
                    SplashScreen(
                        onSplashFinished = { showSplash = false }
                    )
                } else if (!isLoggedIn) {
                    // 1. Check Login State
                    LoginScreen(
                        onLoginSuccess = {
                            viewModel.setLoggedIn(true)
                            Toast.makeText(context, "स्वागत आहे! AKGMM ॲपमध्ये यशस्वी लॉगिन झाले.", Toast.LENGTH_SHORT).show()
                        }
                    )
                } else if (settings.isPinEnabled && !isAppUnlocked) {
                    // 2. Check PIN Lock status
                    LockScreen(
                        settings = settings,
                        onUnlockSuccess = { isAppUnlocked = true }
                    )
                } else {
                    // 3. Main Application Flow with Navigation Drawer
                    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        gesturesEnabled = true,
                        drawerContent = {
                            AppNavigationDrawerContent(
                                currentRoute = currentRoute,
                                settings = settings,
                                onNavigate = { route ->
                                    scope.launch { drawerState.close() }
                                    navController.navigate(route) {
                                        popUpTo("dashboard") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                onCloseDrawer = {
                                    scope.launch { drawerState.close() }
                                },
                                onLockClicked = {
                                    scope.launch { drawerState.close() }
                                    if (settings.isPinEnabled) {
                                        isAppUnlocked = false
                                    } else {
                                        Toast.makeText(context, "PIN लॉक चालू करण्यासाठी सेटिंग्जमध्ये जा.", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                onAiAssistantClicked = {
                                    scope.launch { drawerState.close() }
                                    showAiAssistantDialog = true
                                },
                                onReconciliationClicked = {
                                    scope.launch { drawerState.close() }
                                    showReconciliationDialog = true
                                },
                                onBackupClicked = {
                                    scope.launch { drawerState.close() }
                                    navController.navigate("settings")
                                },
                                onMembersClicked = {
                                    scope.launch { drawerState.close() }
                                    navController.navigate("settings")
                                },
                                onLogoutClicked = {
                                    scope.launch { drawerState.close() }
                                    viewModel.logout()
                                    Toast.makeText(context, "यशस्वीरीत्या लॉगआउट झाले.", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    ) {
                        Scaffold(
                            containerColor = OrangeBackground,
                            topBar = {
                                if (currentRoute != "settings") {
                                    AppTopBar(
                                        currentTitle = when (currentRoute) {
                                            "dashboard" -> "डॅशबोर्ड"
                                            "vargani" -> "वर्गणी संकलन"
                                            "expenses" -> "मंडळ खर्च"
                                            "pending" -> "शिल्लक वर्गणी"
                                            "reports" -> "ताळेबंद अहवाल"
                                            "calendar" -> "उत्सव कॅलेंडर"
                                            "daily_closing" -> "दैनिक हिशोब"
                                            "audit" -> "ऑडिट हिस्ट्री"
                                            else -> "AKGMM"
                                        },
                                        festivalYear = settings.festivalYear,
                                        isAppLocked = !isAppUnlocked,
                                        onMenuClicked = {
                                            scope.launch { drawerState.open() }
                                        },
                                        onLockClicked = {
                                            if (settings.isPinEnabled) {
                                                isAppUnlocked = false
                                            } else {
                                                Toast.makeText(context, "PIN लॉक चालू करण्यासाठी सेटिंग्जमध्ये जा.", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        onAiAssistantClicked = { showAiAssistantDialog = true },
                                        onSettingsClicked = { navController.navigate("settings") },
                                        onLogoutClicked = {
                                            viewModel.logout()
                                            Toast.makeText(context, "यशस्वीरीत्या लॉगआउट झाले.", Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                            },
                            bottomBar = {
                                if (currentRoute != "settings") {
                                    AppBottomNavigationBar(
                                        currentRoute = currentRoute,
                                        onNavigate = { route ->
                                            navController.navigate(route) {
                                                popUpTo("dashboard") { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    )
                                }
                            }
                        ) { innerPadding ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            ) {
                                NavHost(
                                    navController = navController,
                                    startDestination = "dashboard"
                                ) {
                                    // 1. Dashboard Screen
                                    composable("dashboard") {
                                        DashboardScreen(
                                            settings = settings,
                                            totalVargani = totalVargani,
                                            totalOwnerVargani = totalOwnerVargani,
                                            totalTenantVargani = totalTenantVargani,
                                            totalOtherVargani = totalOtherVargani,
                                            ownerCount = ownerCount,
                                            tenantCount = tenantCount,
                                            otherCount = otherCount,
                                            totalExpenses = totalExpense,
                                            netBalance = netBalance,
                                            todayVargani = todayVargani,
                                            todayExpenses = todayExpenses,
                                            netCashInHand = netCashInHand,
                                            netUpiInBank = netUpiInBank,
                                            recentVargani = varganiList,
                                            recentExpenses = expenseList,
                                            events = festivalEvents,
                                            onNavigateToVargani = { navController.navigate("vargani") },
                                            onNavigateToExpenses = { navController.navigate("expenses") },
                                            onNavigateToPending = { navController.navigate("pending") },
                                            onNavigateToReports = { navController.navigate("reports") },
                                            onNavigateToCalendar = { navController.navigate("calendar") },
                                            onAddNewPavti = {
                                                scope.launch {
                                                    nextPavtiNumber = viewModel.getNextPavtiNumber()
                                                    editingVargani = null
                                                    showAddVarganiDialog = true
                                                }
                                            },
                                            onAddNewExpense = {
                                                editingExpense = null
                                                showAddExpenseDialog = true
                                            },
                                            onOpenDailyClosing = { showDailyClosingDialog = true },
                                            onOpenReconciliation = { showReconciliationDialog = true },
                                            onOpenFinalReport = { showFinalReportDialog = true },
                                            onOpenAuditHistory = { showAuditHistoryDialog = true },
                                            onSelectPavti = { pavti -> selectedPavtiForDetail = pavti },
                                            onShareSummaryWhatsApp = {
                                                ShareHelper.shareWhatsAppSummary(
                                                    context = context,
                                                    settings = settings,
                                                    totalVargani = totalVargani,
                                                    totalOwnerVargani = totalOwnerVargani,
                                                    totalTenantVargani = totalTenantVargani,
                                                    totalOtherVargani = totalOtherVargani,
                                                    ownerCount = ownerCount,
                                                    tenantCount = tenantCount,
                                                    otherCount = otherCount,
                                                    totalExpenses = totalExpense,
                                                    netBalance = netBalance
                                                )
                                            }
                                        )
                                    }

                                // 2. Vargani Screen
                                composable("vargani") {
                                    VarganiScreen(
                                        varganiList = varganiList,
                                        ownerWiseRecords = ownerWiseRecords,
                                        allOwnerNames = allOwnerNames,
                                        settings = settings,
                                        totalVarganiAmount = totalVargani,
                                        totalOwnerVargani = totalOwnerVargani,
                                        totalTenantVargani = totalTenantVargani,
                                        totalOtherVargani = totalOtherVargani,
                                        ownerCount = ownerCount,
                                        tenantCount = tenantCount,
                                        otherCount = otherCount,
                                        onAddNewPavti = {
                                            scope.launch {
                                                nextPavtiNumber = viewModel.getNextPavtiNumber()
                                                editingVargani = null
                                                showAddVarganiDialog = true
                                            }
                                        },
                                        onSelectPavti = { pavti -> selectedPavtiForDetail = pavti },
                                        onQuickWhatsApp = { pavti ->
                                            ShareHelper.shareWhatsAppReceipt(context, pavti, settings)
                                        },
                                        onQuickPdf = { pavti ->
                                            val pdfFile = PdfReceiptGenerator.generateVarganiReceiptPdf(context, pavti, settings)
                                            if (pdfFile != null) {
                                                ShareHelper.sharePdfFile(context, pdfFile, "AKGMM पावती - ${pavti.pavtiNumber}")
                                            } else {
                                                Toast.makeText(context, "PDF तयार करताना त्रुटी आली.", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        onExportExcel = {
                                            val file = ExcelReportGenerator.generateVarganiExcel(context, varganiList, settings)
                                            if (file != null) {
                                                ShareHelper.shareExcelFile(context, file, "AKGMM वर्गणी अहवाल")
                                            }
                                        }
                                    )
                                }

                                // 3. Expenses Screen
                                composable("expenses") {
                                    ExpensesScreen(
                                        settings = settings,
                                        expenseList = expenseList,
                                        onOpenAddExpense = {
                                            editingExpense = null
                                            showAddExpenseDialog = true
                                        },
                                        onEditExpense = { exp ->
                                            editingExpense = exp
                                            showAddExpenseDialog = true
                                        },
                                        onDeleteExpense = { expId ->
                                            val exp = expenseList.find { it.id == expId }
                                            if (exp != null) {
                                                viewModel.deleteExpense(exp)
                                                Toast.makeText(context, "खर्च रद्द करण्यात आला.", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        onSettleAdvance = { advanceExp ->
                                            editingExpense = ExpenseTransaction(
                                                title = "${advanceExp.title} (अंतिम हिशोब)",
                                                category = advanceExp.category,
                                                amount = 0.0,
                                                paidTo = advanceExp.paidTo,
                                                paymentMode = "रोख",
                                                expenseType = "FINAL_SETTLEMENT",
                                                linkedAdvanceId = advanceExp.id,
                                                advancePaidAmount = advanceExp.amount,
                                                totalEstimatedCost = if (advanceExp.totalEstimatedCost > 0) advanceExp.totalEstimatedCost else advanceExp.amount,
                                                memberAttribution = advanceExp.memberAttribution,
                                                isMahaprasad = advanceExp.isMahaprasad
                                            )
                                            showAddExpenseDialog = true
                                        }
                                    )
                                }

                                // 4. Pending Vargani Screen
                                composable("pending") {
                                    PendingVarganiScreen(
                                        pendingList = pendingList,
                                        onAddNewPending = { showAddPendingDialog = true },
                                        onCollectPending = { pending ->
                                            scope.launch {
                                                val nextNo = viewModel.getNextPavtiNumber()
                                                viewModel.markPendingAsCollected(pending, nextNo)
                                                Toast.makeText(context, "पावती तयार केली: $nextNo", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        onDeletePending = { pending ->
                                            viewModel.deletePendingVargani(pending)
                                            Toast.makeText(context, "शिल्लक नोंद हटवली.", Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }

                                // 5. Reports Screen
                                composable("reports") {
                                    ReportsScreen(
                                        settings = settings,
                                        varganiList = varganiList,
                                        expenseList = expenseList,
                                        incomeList = incomeList,
                                        totalVargani = totalVargani,
                                        totalOwnerVargani = totalOwnerVargani,
                                        totalTenantVargani = totalTenantVargani,
                                        totalOtherVargani = totalOtherVargani,
                                        ownerCount = ownerCount,
                                        tenantCount = tenantCount,
                                        otherCount = otherCount,
                                        totalExpense = totalExpense,
                                        netBalance = netBalance,
                                        onOpenFinalReport = { showFinalReportDialog = true },
                                        onOpenDailyClosing = { showDailyClosingDialog = true },
                                        onOpenReconciliation = { showReconciliationDialog = true },
                                        onOpenAuditHistory = { showAuditHistoryDialog = true },
                                        onExportPdfReport = {
                                            val file = PdfReceiptGenerator.generateFinancialReportPdf(
                                                context = context,
                                                settings = settings,
                                                varganiList = varganiList,
                                                expenseList = expenseList,
                                                incomeList = incomeList,
                                                pendingList = pendingList
                                            )
                                            if (file != null) {
                                                ShareHelper.sharePdfFile(context, file, "AKGMM ताळेबंद अहवाल (${settings.festivalYear})")
                                            } else {
                                                Toast.makeText(context, "PDF अहवाल तयार करताना त्रुटी आली.", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        onExportAllVarganiExcel = {
                                            val file = ExcelReportGenerator.generateVarganiExcel(context, varganiList, settings, "सर्व वर्गणी")
                                            if (file != null) {
                                                ShareHelper.shareExcelFile(context, file, "AKGMM वर्गणी यादी")
                                            }
                                        },
                                        onExportOwnerVarganiExcel = {
                                            val owners = varganiList.filter { it.isOwner }
                                            val file = ExcelReportGenerator.generateVarganiExcel(context, owners, settings, "फक्त घरमालक वर्गणी")
                                            if (file != null) {
                                                ShareHelper.shareExcelFile(context, file, "AKGMM घरमालक वर्गणी यादी")
                                            }
                                        },
                                        onExportTenantVarganiExcel = {
                                            val tenants = varganiList.filter { it.isTenant }
                                            val file = ExcelReportGenerator.generateVarganiExcel(context, tenants, settings, "फक्त भाडेकरू वर्गणी")
                                            if (file != null) {
                                                ShareHelper.shareExcelFile(context, file, "AKGMM भाडेकरू वर्गणी यादी")
                                            }
                                        },
                                        onExportOtherVarganiExcel = {
                                            val others = varganiList.filter { it.isOther }
                                            val file = ExcelReportGenerator.generateVarganiExcel(context, others, settings, "फक्त इतर देणगीदार वर्गणी")
                                            if (file != null) {
                                                ShareHelper.shareExcelFile(context, file, "AKGMM इतर देणगीदार वर्गणी यादी")
                                            }
                                        },
                                        onExportFullAccountingExcel = {
                                            val file = ExcelReportGenerator.generateFullAccountingExcel(
                                                context = context,
                                                varganiList = varganiList,
                                                expenseList = expenseList,
                                                settings = settings,
                                                dailyClosingList = dailyClosings,
                                                reconciliationList = reconciliations,
                                                auditLogs = auditLogs
                                            )
                                            if (file != null) {
                                                ShareHelper.shareExcelFile(context, file, "AKGMM संपूर्ण ९-शीट हिशोब वही")
                                            }
                                        },
                                        onShareSummaryWhatsApp = {
                                            ShareHelper.shareWhatsAppSummary(
                                                context = context,
                                                settings = settings,
                                                totalVargani = totalVargani,
                                                totalOwnerVargani = totalOwnerVargani,
                                                totalTenantVargani = totalTenantVargani,
                                                totalOtherVargani = totalOtherVargani,
                                                ownerCount = ownerCount,
                                                tenantCount = tenantCount,
                                                otherCount = otherCount,
                                                totalExpenses = totalExpense,
                                                netBalance = netBalance
                                            )
                                        }
                                    )
                                }

                                // 6. Settings Screen
                                composable("settings") {
                                    SettingsScreen(
                                        settings = settings,
                                        onSaveSettings = { newSettings ->
                                            viewModel.saveSettings(newSettings) {
                                                Toast.makeText(context, "सेटिंग्ज जतन केल्या!", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        onExportBackup = {
                                            viewModel.createBackupJson { json ->
                                                if (json != null) {
                                                    val file = BackupManager.saveBackupFile(context, json)
                                                    if (file != null) {
                                                        BackupManager.shareBackupFile(context, file)
                                                    }
                                                }
                                            }
                                        },
                                        onImportBackupDialog = {
                                            Toast.makeText(context, "बॅकअप रिस्टोअर करण्यासाठी फाइल सिलेक्ट करा.", Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }

                                // 7. Festival Calendar & Events Schedule Screen
                                composable("calendar") {
                                    CalendarScheduleScreen(
                                        viewModel = viewModel
                                    )
                                }

                                // 8. Daily Closing & Cash Lock Screen
                                composable("daily_closing") {
                                    DailyClosingScreen(
                                        viewModel = viewModel
                                    )
                                }

                                // 9. Audit History & Activity Logs Screen
                                composable("audit") {
                                    AuditHistoryScreen(
                                        auditLogs = auditLogs,
                                        onBack = { navController.popBackStack() }
                                    )
                                }
                            }
                        }
                    }

                    // --- DIALOGS ---

                    // Add / Edit Vargani Dialog
                    if (showAddVarganiDialog) {
                        AddVarganiDialog(
                            initialVargani = editingVargani,
                            nextPavtiNumber = nextPavtiNumber,
                            collectorName = settings.authorizedSignatory,
                            knownOwnerNames = allOwnerNames,
                            onDismiss = {
                                showAddVarganiDialog = false
                                editingVargani = null
                            },
                            onSave = { vargani ->
                                if (editingVargani == null) {
                                    viewModel.addVargani(
                                        vargani = vargani,
                                        onSuccess = { saved ->
                                            showAddVarganiDialog = false
                                            selectedPavtiForDetail = saved
                                            Toast.makeText(context, "पावती ${saved.pavtiNumber} जतन केली!", Toast.LENGTH_SHORT).show()
                                        },
                                        onError = { err ->
                                            Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                                        }
                                    )
                                } else {
                                    viewModel.updateVargani(
                                        vargani = vargani,
                                        onSuccess = {
                                            showAddVarganiDialog = false
                                            editingVargani = null
                                            selectedPavtiForDetail = vargani
                                            Toast.makeText(context, "पावती बदल जतन झाले!", Toast.LENGTH_SHORT).show()
                                        },
                                        onError = { err ->
                                            Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                                        }
                                    )
                                }
                            }
                        )
                    }

                    // Add Expense Dialog
                    if (showAddExpenseDialog) {
                        AddExpenseDialog(
                            initialExpense = editingExpense,
                            unsettledAdvances = unsettledAdvances,
                            executiveMembers = executiveMembers,
                            onDismiss = {
                                showAddExpenseDialog = false
                                editingExpense = null
                            },
                            onSave = { exp ->
                                if (editingExpense == null || editingExpense?.id == 0L) {
                                    viewModel.addExpense(
                                        expense = exp,
                                        onSuccess = {
                                            showAddExpenseDialog = false
                                            editingExpense = null
                                            Toast.makeText(context, "खर्च नोंदवला!", Toast.LENGTH_SHORT).show()
                                        },
                                        onError = { err ->
                                            Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                                        }
                                    )
                                } else {
                                    viewModel.updateExpense(
                                        expense = exp,
                                        onSuccess = {
                                            showAddExpenseDialog = false
                                            editingExpense = null
                                            Toast.makeText(context, "खर्च बदल जतन झाले!", Toast.LENGTH_SHORT).show()
                                        },
                                        onError = { err ->
                                            Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                                        }
                                    )
                                }
                            }
                        )
                    }

                    // Daily Closing Dialog
                    if (showDailyClosingDialog) {
                        DailyClosingDialog(
                            varganiList = varganiList,
                            expenseList = expenseList,
                            dailyClosingList = dailyClosings,
                            currentUser = currentUser.ifBlank { settings.authorizedSignatory.ifBlank { "खजिनदार" } },
                            onCloseDay = { dateStr, notes, closedBy ->
                                viewModel.closeDay(
                                    dateString = dateStr,
                                    notes = notes,
                                    closedBy = closedBy,
                                    onSuccess = {
                                        Toast.makeText(context, "दिवसाचा हिशोब यशस्वीरीत्या बंद झाला!", Toast.LENGTH_SHORT).show()
                                    },
                                    onError = { err ->
                                        Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            },
                            onReopenDay = { dateStr ->
                                viewModel.reopenDay(
                                    dateString = dateStr,
                                    reopenedBy = currentUser,
                                    onSuccess = {
                                        Toast.makeText(context, "दिवस पुन्हा अनलॉक करण्यात आला!", Toast.LENGTH_SHORT).show()
                                    },
                                    onError = { err ->
                                        Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            },
                            onDismiss = { showDailyClosingDialog = false }
                        )
                    }

                    // Cash & UPI Reconciliation Dialog
                    if (showReconciliationDialog) {
                        ReconciliationDialog(
                            systemCash = netCashInHand,
                            systemUpi = netUpiInBank,
                            reconciliationList = reconciliations,
                            currentUser = currentUser.ifBlank { settings.authorizedSignatory.ifBlank { "खजिनदार" } },
                            onSaveReconciliation = { physicalCash, notes ->
                                viewModel.performCashReconciliation(
                                    physicalCash = physicalCash,
                                    notes = notes,
                                    performedBy = currentUser,
                                    onSuccess = {
                                        Toast.makeText(context, "रोख ताळमेळ जतन झाला!", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            },
                            onDismiss = { showReconciliationDialog = false }
                        )
                    }

                    // Final Festival Report Dialog
                    if (showFinalReportDialog) {
                        FinalReportDialog(
                            settings = settings,
                            varganiList = varganiList,
                            expenseList = expenseList,
                            incomeList = incomeList,
                            totalVargani = totalVargani,
                            totalOwnerVargani = totalOwnerVargani,
                            totalTenantVargani = totalTenantVargani,
                            ownerCount = ownerCount,
                            tenantCount = tenantCount,
                            totalExpense = totalExpense,
                            netBalance = netBalance,
                            totalCashVargani = totalCashVargani,
                            totalUpiVargani = totalUpiVargani,
                            totalCashExpense = totalCashExpense,
                            totalUpiExpense = totalUpiExpense,
                            netCashInHand = netCashInHand,
                            netUpiInBank = netUpiInBank,
                            onDismiss = { showFinalReportDialog = false },
                            onExportPdf = {
                                val file = PdfReceiptGenerator.generateFinancialReportPdf(
                                    context = context,
                                    settings = settings,
                                    varganiList = varganiList,
                                    expenseList = expenseList,
                                    incomeList = incomeList,
                                    pendingList = pendingList
                                )
                                if (file != null) {
                                    ShareHelper.sharePdfFile(context, file, "AKGMM अंतिम ताळेबंद अहवाल (${settings.festivalYear})")
                                } else {
                                    Toast.makeText(context, "PDF तयार करताना त्रुटी आली.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onExportExcel = {
                                val file = ExcelReportGenerator.generateFullAccountingExcel(
                                    context = context,
                                    varganiList = varganiList,
                                    expenseList = expenseList,
                                    settings = settings,
                                    dailyClosingList = dailyClosings,
                                    reconciliationList = reconciliations,
                                    auditLogs = auditLogs
                                )
                                if (file != null) {
                                    ShareHelper.shareExcelFile(context, file, "AKGMM अंतिम ९-शीट हिशोब वही")
                                }
                            },
                            onShareWhatsApp = {
                                ShareHelper.shareWhatsAppSummary(
                                    context = context,
                                    settings = settings,
                                    totalVargani = totalVargani,
                                    totalOwnerVargani = totalOwnerVargani,
                                    totalTenantVargani = totalTenantVargani,
                                    ownerCount = ownerCount,
                                    tenantCount = tenantCount,
                                    totalExpenses = totalExpense,
                                    netBalance = netBalance
                                )
                            }
                        )
                    }

                    // Audit History Dialog
                    if (showAuditHistoryDialog) {
                        Dialog(
                            onDismissRequest = { showAuditHistoryDialog = false },
                            properties = DialogProperties(usePlatformDefaultWidth = false)
                        ) {
                            AuditHistoryScreen(
                                auditLogs = auditLogs,
                                onBack = { showAuditHistoryDialog = false }
                            )
                        }
                    }

                    // Add Pending Vargani Dialog
                    if (showAddPendingDialog) {
                        AddPendingDialog(
                            onDismiss = { showAddPendingDialog = false },
                            onSave = { pending ->
                                viewModel.addPendingVargani(
                                    pending = pending,
                                    onSuccess = {
                                        showAddPendingDialog = false
                                        Toast.makeText(context, "शिल्लक वर्गणी नोंदवली!", Toast.LENGTH_SHORT).show()
                                    },
                                    onError = { err ->
                                        Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                                    }
                                )
                            }
                        )
                    }

                    // AI Assistant Dialog
                    if (showAiAssistantDialog) {
                        AiAssistantDialog(
                            settings = settings,
                            chatHistory = chatMessages,
                            isLoading = false,
                            onSendMessage = { query -> viewModel.sendAiMessage(query) },
                            onClearChat = { /* clear chat */ },
                            onDismiss = { showAiAssistantDialog = false }
                        )
                    }

                    // Pavti Detail Sheet
                    selectedPavtiForDetail?.let { pavti ->
                        PavtiDetailSheet(
                            vargani = pavti,
                            settings = settings,
                            sheetState = pavtiSheetState,
                            onDismiss = { selectedPavtiForDetail = null },
                            onShareWhatsApp = { p ->
                                ShareHelper.shareWhatsAppReceipt(context, p, settings)
                            },
                            onExportPdf = { p ->
                                val pdf = PdfReceiptGenerator.generateVarganiReceiptPdf(context, p, settings)
                                if (pdf != null) {
                                    ShareHelper.sharePdfFile(context, pdf, "AKGMM पावती - ${p.pavtiNumber}")
                                } else {
                                    Toast.makeText(context, "PDF तयार करताना त्रुटी आली.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onDelete = { p ->
                                viewModel.deleteVargani(p)
                                selectedPavtiForDetail = null
                                Toast.makeText(context, "पावती रद्द केली गेली.", Toast.LENGTH_SHORT).show()
                            },
                            onEdit = { p ->
                                selectedPavtiForDetail = null
                                editingVargani = p
                                showAddVarganiDialog = true
                            }
                        )
                    }
                    }
                }
            }
        }
    }
}
