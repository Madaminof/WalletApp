package dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.categories

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.samandar.walletapp.R


@Composable
fun getTranslatedName(dbName: String): Any {
    val resId = when (dbName) {

        "Cash" -> R.string.acc_cash
        "Card" -> R.string.acc_card

        // --- 1. FOOD & DRINK ---
        "Groceries" -> R.string.cat_groceries
        "Dining out" -> R.string.cat_dining_out
        "Drinks & Coffee" -> R.string.cat_drinks_coffee
        "Restaurant" -> R.string.cat_restaurant

        // --- 2. HOME & LIVING ---
        "Housing" -> R.string.cat_housing
        "Utilities" -> R.string.cat_utilities
        "Maintenance" -> R.string.cat_maintenance
        "Rent" -> R.string.cat_rent

        // --- 3. LIFESTYLE & LEISURE ---
        "Entertainment" -> R.string.cat_entertainment
        "Subscriptions" -> R.string.cat_subscriptions
        "Self-Care" -> R.string.cat_self_care
        "Hobbies" -> R.string.cat_hobbies

        // --- 4. EDUCATION & GROWTH ---
        "Education" -> R.string.cat_education
        "Books" -> R.string.cat_books
        "Courses" -> R.string.cat_courses
        "Certifications" -> R.string.cat_certifications

        // --- 5. SHOPPING ---
        "Shopping" -> R.string.cat_shopping
        "Clothing" -> R.string.cat_clothing
        "Electronics" -> R.string.cat_electronics
        "Home Appliances" -> R.string.cat_home_appliances

        // --- 6. HEALTH & WELLNESS ---
        "Health" -> R.string.cat_health
        "Fitness" -> R.string.cat_fitness
        "Pharmacy" -> R.string.cat_pharmacy
        "Dental" -> R.string.cat_dental

        // --- 7. FINANCIAL & OTHER ---
        "Investment" -> R.string.cat_investment
        "Debt & Loans" -> R.string.cat_debt_loans
        "Gifts & Donation" -> R.string.cat_gifts_donation
        "Other" -> R.string.cat_other

        // --- 8. INCOME ---
        "Main Salary" -> R.string.cat_main_salary
        "Side Job" -> R.string.cat_side_job
        "Business" -> R.string.cat_business
        "Freelance" -> R.string.cat_freelance
        "Passive Income" -> R.string.cat_passive_income
        "Dividends/Interest" -> R.string.cat_dividends
        "Bonus" -> R.string.cat_bonus
        "Cashback" -> R.string.cat_cashback
        "Grants/Scholarship" -> R.string.cat_grants
        "Gifts" -> R.string.cat_gifts
        "Other Income" -> R.string.cat_other_income

        // --- 9. DEBT SPECIALS ---
        "Lent" -> R.string.cat_lent
        "Borrowed" -> R.string.cat_borrowed
        "Debt Payment" -> R.string.cat_debt_payment
        "Transport" -> R.string.cat_transport
        "Taxi" -> R.string.cat_taxi
        "Fuel" -> R.string.cat_fuel
        "Car Maintenance" -> R.string.cat_car_maintenance

        else -> null
    }

    return resId?.let { stringResource(it) } ?: dbName
}