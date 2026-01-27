package dev.samandar.walletapp.navigation


sealed class Screen(val route: String) {

    object Splash: Screen("splash")

    object Home : Screen("home")
    object Charts : Screen("charts")
    object Wallet : Screen("wallet")
    object Category : Screen("categories")
    object Budgets : Screen("budgets")
    object ShoppingLists : Screen("shopping_lists")
    object Goals : Screen("goals")
    object ExpenseList: Screen("expense_list")
    object DebtsScreen: Screen("debts_screen")
    object SettingScreen: Screen("settings")
    object CategoryStatisticsScreen: Screen("categoryStatistic")
    object CategoryDetail : Screen("categoryDetail/{categoryName}/{transactionType}") {
        fun passArgs(name: String, type: String): String {
            return "categoryDetail/$name/$type"
        }
    }
    object Add : Screen("add")
    object budjetAdd : Screen("add_budjet")
    object addTransaction: Screen("add_transaction")
    object addAccound: Screen("add_accound")

    object AddEditDebt : Screen("add_edit_debt")
    object editAccount : Screen("editAccount")


    object detailTransaction : Screen("detail")
    object BudgetDetail : Screen("budget_detail/{budgetId}") {
        fun createRoute(budgetId: Int) = "budget_detail/$budgetId"
    }
    object DebtDetail : Screen("debt_detail/{debtId}") {
        fun createRoute(debtId: String) = "debt_detail/$debtId"
    }
    object ShoppingDetail : Screen("shopDetail")



    object SCANNER: Screen("scanner")
    object REVIEW :Screen("review")

}