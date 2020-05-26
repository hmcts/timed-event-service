provider "azurerm" {
  features {}
}

module "ia_timed_event_service_database" {
  source             = "git@github.com:hmcts/cnp-module-postgres?ref=master"
  product            = "${var.product}-${var.component}-postgres-db"
  location           = "${var.location}"
  env                = "${var.env}"
  database_name      = "${var.postgresql_database_name}"
  postgresql_user    = "${var.postgresql_user}"
  postgresql_version = "10"
  common_tags        = "${merge(var.common_tags, map("lastUpdated", "${timestamp()}"))}"
  subscription       = "${var.subscription}"
}

resource "azurerm_key_vault_secret" "POSTGRES-PASS" {
  name         = "${var.component}-POSTGRES-PASS"
  value        = "${module.ia_timed_event_service_database.postgresql_password}"
  key_vault_id = "${data.azurerm_key_vault.ia_key_vault.id}"
}
