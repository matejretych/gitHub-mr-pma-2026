# PMA 2026

## Projekty

- `task-002-guess-number`: Uhodni cislo, XML a Kotlin (ukol 002).
- `task-003-dice-xml`: Hod kostkou, Empty Views Activity s `findViewById`.
- `task-003-dice-compose`: Hod kostkou, Empty Activity s Jetpack Compose.
- `task-004-coffee-order`: Objednavka kavy, XML a View Binding.
- `task-005-coffee-order-v2`: Objednavka kavy V2, ConstraintLayout a dve Activity.

Kazdou slozku otevri v Android Studiu jako samostatny projekt a spust na
emulatoru nebo telefonu s Androidem 7.0 (API 24) nebo vyssim. Projekty pouzivaji
SDK 37 a Gradle Wrapper. Lokalni cestu k SDK nastavi Android Studio pri otevreni.

## Ukol 003: Hod kostkou

Obe aplikace zobrazuji nadpis "Hoď kostkou", symbol kostky a tlacitko "Hodit".
Po stisku probehne deset nahodnych zmen symbolu s prodlevou 250 ms. Po 2,5 s
se vylosuje konecny vysledek. Tlacitko je po celou animaci zakazane.
Obsah je vystredeny a respektuje systemove listy.

### XML vs. Compose

| XML + Kotlin | Jetpack Compose |
| --- | --- |
| Rozlozeni je v `activity_main.xml`. | Rozlozeni sestavuje funkce `DiceScreen` v Kotlinu. |
| `findViewById` ziska reference na TextView a Button. | Composable funkce vytvareji prvky primo, bez XML ID. |
| Kazdy mezihod explicitne priradi symbol do `diceText.text`. | Zmena `diceValue` ve sledovanem stavu automaticky vyvola rekompozici a aktualizuje `Text`. |
| `isEnabled` primo zapina a vypina tlacitko. | `enabled = !isRolling` odvozuje stav tlacitka ze stavu obrazovky. |
| `lifecycleScope` zrusi animaci pri zniceni Activity. | `rememberCoroutineScope` zrusi animaci pri opusteni kompozice. |

### Vylepseni

Pocitadlo dokoncenych hodu a posledni vysledek. Tyto hodnoty i symbol kostky
preziji otoceni zarizeni: XML pouziva `onSaveInstanceState`, Compose
`rememberSaveable`. Probihajici animace se pri otoceni zrusi; tlacitko je
na nove obrazovce opet dostupne. Symbol ma take textovy popis pro pristupnost.

### Screenshoty

![XML aplikace](screenshots/dice-xml.png)

![Compose aplikace](screenshots/dice-compose.png)

Zadani: https://github.com/zizanek/github-tz-pma-2026/issues/3

## Ukol 004: Objednavka kavy

Projekt `MyApp004Objednavka` ve slozce `task-004-coffee-order` pouziva
LinearLayout a View Binding. RadioButton vybira espresso, cappuccino nebo
latte a meni vlastni vektorovou ilustraci v `drawable`. Checkboxy pridavaji
ovesne mleko, vanilkovy sirup a extra davku espressa. Tlacitko Objednat
vypise zvolenou konfiguraci do souhrnu.

Vlastni rozsireni: pocet kusu (1 az 10) a okamzity prepocet ceny vcetne
doplnku ke kazdemu napoji. Souhrn zachycuje posledni potvrzenou objednavku.
Rozpracovana konfigurace i souhrn zustavaji zachovane pri otoceni zarizeni.

Texty jsou ve `values/strings.xml` anglicky (fallback), ve `values-cs/`
cesky a ve `values-de/` nemecky. Jazyk urcuje nastaveni zarizeni.
Ceny zustavaji v CZK, jejich zapis se ridi zvolenym jazykem.

Zadani: https://github.com/zizanek/github-tz-pma-2026/issues/4

Screenshot vlastniho rozsireni pro odevzdani:

![Pocet kusu, vypocet ceny a souhrn objednavky](screenshots/coffee-order-extension-cs.png)

## Ukol 005: Objednavka kavy V2

Novy projekt `MyApp005ObjednavkaV2` ve slozce `task-005-coffee-order-v2`
navazuje na ukol 004. Formular pouziva ConstraintLayout, RadioGroup,
CheckBoxy a okamzity prepocet ceny pomoci lambda posluchacu.
View Binding propojuje obe obrazovky s Kotlinem.

Tlacitko Zkontrolovat objednavku predava vybranou kavu, doplnky, pocet kusu
a celkovou cenu pres explicitni Intent do `SummaryActivity`.
Ta cte `getStringExtra`, `getBooleanExtra` a `getIntExtra` a zobrazi rekapitulaci.
ScrollView s `fillViewport="true"` obsahuje vnitrni ConstraintLayout a dlouhy
informacni text. Tlacitko Zpet k uprave je mimo scrollovany obsah, ukotvene
ke spodni hrane obrazovky nad systemovou listou; vola `finish()`.
Vyber se pri navratu zachova. Preklady EN, CS a DE navazuji na ukol 004.

### Moznosti trvaleho ukladani

- **Room (nad SQLite):** vhodne pro historii objednavek, razeni a vyhledavani.
  Data by predstavovala entita objednavky, zapis a dotazy by zajistovalo DAO.
  [Dokumentace Room](https://developer.android.com/training/data-storage/room).
- **DataStore:** vhodne pro male mnozstvi dat, napr. posledni rozpracovany vyber
  nebo preference. Podporuje asynchronni a transakcni aktualizace, ale pro
  slozitejsi historii a castecne aktualizace je vhodnejsi Room.
  [Dokumentace DataStore](https://developer.android.com/topic/libraries/architecture/datastore).
- **Soubor JSON v internim ulozisti:** jednoducha moznost pro maly seznam,
  ale aplikace musi sama resit serializaci, bezpecne prepisovani a zmeny formatu.
  Interni soubory nevyzaduji opravneni a pri odinstalaci se odstrani.
  [Dokumentace souboru](https://developer.android.com/training/data-storage/app-specific).

Pro budouci historii bych zvolil Room. Ukladal by se identifikator, cas,
typ kavy, doplnky, pocet kusu a cena platna v okamziku objednavky.
Zmena ceniku tak nezmeni stare objednavky. Zapis by probihal v korutine mimo
hlavni vlakno. Trvale ukladani zatim neni implementovano: navazny ukol
pozaduje pruzkum moznosti. Intent a ulozeny stav Activity nejsou databaze.

Zadani: https://github.com/zizanek/github-tz-pma-2026/issues/5

![Vyber kavy V2](screenshots/coffee-v2-selection.png)

![Rekapitulace objednavky V2](screenshots/coffee-v2-summary.png)
