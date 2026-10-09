package com.example.data

import com.example.model.*

object SampleData {

    val mockUsers = listOf(
        User(
            id = "user_viewer",
            name = "Sopheak Chen",
            email = "sopheak.anime@gmail.com",
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
            role = UserRole.VIEWER,
            bio = "Anime enthusiast | Watching recaps on commute"
        ),
        User(
            id = "user_creator",
            name = "Vannak Anime Recap (វណ្ណៈ Recap)",
            email = "vannak.recap@gmail.com",
            avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
            role = UserRole.CREATOR,
            channelName = "Vannak Recap KH",
            subscribersCount = 42800,
            bio = "សម្រាយសាច់រឿង Anime & Manhwa ពេញនិយម រៀបរាប់លម្អិត Rank និង Faction តាមក្បួនខ្នាត!"
        ),
        User(
            id = "user_admin",
            name = "Dara Platform Admin",
            email = "admin@anirecap.kh",
            avatarUrl = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=150",
            role = UserRole.ADMIN,
            bio = "AniRecap System Administrator & Content Quality Lead"
        )
    )

    val sampleRecaps = listOf(
        RecapVideo(
            id = "recap_solo_leveling",
            title = "សម្រាយរឿង Solo Leveling - ពី Hunter Rank E ខ្សោយបំផុត ក្លាយជាអធិរាជស្រមោល Shadow Monarch",
            originalTitle = "Solo Leveling (나 혼자만 레벨업)",
            type = VideoType.ANIME,
            seasonEpisode = "Season 1 (Episodes 1 - 12)",
            year = 2024,
            genres = listOf("Action", "Fantasy", "Isekai / Dungeon", "Supernatural"),
            durationSeconds = 1240, // 20m 40s
            thumbnailUrl = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=600",
            creatorId = "user_creator",
            creatorName = "Vannak Recap KH",
            creatorAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
            viewsCount = 184500,
            likesCount = 14200,
            dislikesCount = 120,
            isApproved = true,
            isFeatured = true,
            synopsis = "សង្ខេបរឿង Solo Leveling រដូវកាលទី១ ពេញលេញ! ដំណើរជីវិតរបស់ Sung Jin-woo ពីអ្នកប្រយុទ្ធខ្សោយបំផុតដែលគេហៅថា អាវុធខ្សោយបំផុតរបស់មនុស្សជាតិ រហូតដល់បើកបានប្រព័ន្ធ System ធ្វើឲ្យគាត់អាចឡើង Level ដោយគ្មានដែនកំណត់។",
            chapters = listOf(
                VideoChapter(0, "Hook & ការណែនាំ Sung Jin-woo", "ជីវិតលំបាកជា Rank E"),
                VideoChapter(185, "Double Dungeon Incident", "ប្រាសាទ Cartenon និងច្បាប់ទាំង 3"),
                VideoChapter(450, "Courage of the Weak", "ការភ្ញាក់ដឹងខ្លួនជា Player និង System"),
                VideoChapter(780, "Job Change Quest & Blood-Red Commander Igris", "ការប្រយុទ្ធជាមួយ Igris"),
                VideoChapter(1080, "Arise: កំណើតកងទ័ពស្រមោល Shadow Monarch", "ការគ្រប់គ្រងកងទ័ពស្រមោលដំបូង")
            ),
            ranks = listOf(
                RankHierarchy(
                    entityName = "Sung Jin-woo",
                    category = "Individual Hunter Rank",
                    rankSystem = "E / D / C / B / A / S / National",
                    currentRank = "Rank E (ដំបូង) -> Rank S / Shadow Monarch",
                    significance = "Hunter ធម្មតាមិនអាចឡើង Rank បានទេ ប៉ុន្តែ Jin-woo គឺជា Player តែម្នាក់គត់ដែលមាន System អាចឡើង Level បាន"
                ),
                RankHierarchy(
                    entityName = "Blood-Red Commander Igris",
                    category = "Monster Threat Level / Boss",
                    rankSystem = "Dungeon Boss / S-Rank Potential",
                    currentRank = "Elite Knight Grade Shadow",
                    significance = "មេបញ្ជាការដំបូងបង្អស់ដែល Jin-woo អាចដាស់មកជាស្រមោលបម្រើខ្លួន"
                )
            ),
            factions = listOf(
                FactionStatus(
                    factionName = "Korean Hunters Association",
                    factionType = "Government Oversight",
                    reputation = "Official Authority",
                    characterRole = "Registered Hunter (Rank E -> Retested Rank S)",
                    storyImpact = "ជាស្ថាប័នគ្រប់គ្រង Gate និង Hunter ទាំងអស់ទូទាំងប្រទេស"
                ),
                FactionStatus(
                    factionName = "Shadow Army (កងទ័ពស្រមោល)",
                    factionType = "Personal Monarch Army",
                    reputation = "Secret / Undefeated",
                    characterRole = "Supreme Shadow Monarch",
                    storyImpact = "កងទ័ពផ្ទាល់ខ្លួនរបស់ Jin-woo ដែលមានភាពស្មោះត្រង់ និងមិនចេះស្លាប់"
                )
            ),
            khmerScript = KhmerScript(
                recapTitle = "សម្រាយរឿង Solo Leveling - ដំណើរឡើងកូដគ្មានដែនកំណត់",
                shortHook = "ក្មេងប្រុសម្នាក់ដែលគ្រប់គ្នាមើលងាយ និងដាក់ឈ្មោះថា 'អាវុធខ្សោយបំផុតរបស់មនុស្សជាតិ' បែរជាបើកបានសមត្ថភាពសម្ងាត់ដែលអាចឡើង Level ដោយគ្មានដែនកំណត់ និងផ្លាស់ប្តូរជីវិតរបស់គាត់ទាំងស្រុង!",
                introPhrase = "នៅដើមសាច់រឿង គេបានបង្ហាញឲ្យឃើញថា ពិភពលោកត្រូវបានផ្លាស់ប្តូរដោយច្រកទ្វារវិមាត្រ Gate ដែលបានបើកឡើង ដោយនាំមកនូវសត្វចម្លែកជាច្រើន។ ទន្ទឹមនឹងនោះ មនុស្សមួយចំនួនបានភ្ញាក់ដឹងខ្លួនមានអំណាចពិសេសដែលគេហៅថា Hunter។",
                worldExplanation = "នៅក្នុងប្រព័ន្ធពិភពលោកនេះ Hunter ត្រូវបានបែងចែកជាកម្រិតចាប់ពី Rank E ដល់ Rank S។ អ្វីដែលឃោរឃៅបំផុតគឺថា កម្រិតអំណាចដែល Hunter ទទួលបាននៅពេលភ្ញាក់ដឹងខ្លួនដំបូង នឹងស្ថិតនៅកម្រិតនោះរហូត ដោយមិនអាចហ្វឹកហាត់បន្ថែមបានឡើយ។",
                characterIntro = "តួឯករបស់យើងឈ្មោះ ស៊ុង ជីនវូ (Sung Jin-woo) គឺជា Hunter Rank E ដែលខ្សោយបំផុត។ គាត់ត្រូវបង្ខំចិត្តចូលក្នុង Dungeon គ្រោះថ្នាក់ដើម្បីរកប្រាក់បង់ថ្លៃព្យាបាលម្តាយរបស់គាត់ដែលកំពុងគេងសន្លប់ដោយសារជំងឺ Eternal Slumber។",
                mainStoryRecap = "បន្ទាប់ពីចូលក្នុង Raid កម្រិតទាប ក្រុមរបស់ Jin-woo បានរកឃើញច្រកទ្វារសម្ងាត់មួយដែលគេហៅថា Double Dungeon។ នៅទីនោះ ពួកគេត្រូវបានជាប់ក្នុងអន្ទាក់នៃរូបសំណាកយក្ស។ Jin-woo បានប្រើភាពវៃឆ្លាតរបស់គាត់ដើម្បីដោះស្រាយច្បាប់គោរពបូជាទាំង 3 ប៉ុន្តែនៅទីបំផុត គាត់ត្រូវបានមិត្តរួមក្រុមបោះបង់ចោល។ នៅក្នុងវិនាទីចុងក្រោយ មុនពេលត្រូវគេសម្លាប់ ផ្ទាំងអេក្រង់ System មួយបានលេចឡើងនៅចំពោះមុខគាត់!",
                rankSystemBreakdown = "ទោះបីជាពិភពខាងក្រៅនៅតែចាត់ទុកគាត់ជា Rank E ក៏ដោយ Jin-woo ទទួលបានសមត្ថភាព Player ដែលអនុញ្ញាតឲ្យគាត់ធ្វើដំណើរការ Quest និងបង្កើន Stat ដោយផ្ទាល់។ នេះជាការបំបែកច្បាប់ដំបូងបង្អស់នៃប្រព័ន្ធ Hunter!",
                factionDynamics = "នៅពេលដែល Jin-woo កាន់តែខ្លាំង គាត់បានទាក់ទាញចំណាប់អារម្មណ៍ពី Guild ធំៗដូចជា Hunters Guild និង White Tiger Guild។ ប៉ុន្តែគាត់បានជ្រើសរើសមិនចូលរួមជាមួយ Guild ណាទាំងអស់ ដើម្បីលាក់បាំងអាថ៌កំបាំងនៃសមត្ថភាពរបស់គាត់។",
                majorBattleClimax = "នៅក្នុង Job Change Quest គាត់ត្រូវប្រឈមមុខនឹង Blood-Red Commander Igris ដែលជាអ្នកការពារបន្ទប់បល្ល័ង្ក។ ការប្រយុទ្ធនេះតានតឹងខ្លាំងព្រោះ Igris មានល្បឿន និងកម្លាំងខ្លាំងជាង Jin-woo។ Jin-woo ត្រូវប្រើកម្លាំងដៃទទេ និងដកដាវចាក់ចំចំណុចខ្សោយត្រង់ករបស់វា ដើម្បីដណ្តើមជ័យជម្នះ!",
                endingDirection = "បន្ទាប់ពីឈ្នះ Igris និងទប់ទល់នឹងរលកសត្រូវ Jin-woo បានទទួល Title ជា 'Shadow Monarch' និងពាក្យបញ្ជាវេទមន្ត 'Arise' ដែលអាចដាស់វិញ្ញាណសត្រូវដែលស្លាប់ឲ្យក្លាយជាកងទ័ពស្រមោលរបស់ខ្លួន។ នេះជាការបើកទំព័រថ្មីដែលធ្វើឲ្យគាត់ឈានទៅរកកម្រិតអំណាចដែលគ្មាននរណាអាចស្មានដល់!",
                visualActionNotes = "កាមេរ៉ាពង្រីកលើភ្នែកពណ៌ខៀវបញ្ចេញពន្លឺរបស់ Jin-woo នៅពេលគាត់ស្រែកពាក្យ ARISE ហើយស្រមោលខ្មៅចាប់ផ្តើមងើបចេញពីដី។"
            )
        ),
        RecapVideo(
            id = "recap_jjk_shibuya",
            title = "សម្រាយរឿង Jujutsu Kaisen - មហាសង្គ្រាម Shibuya Incident ការបិទជិត Gojo Satoru និងការភ្ញាក់ដឹងខ្លួន Sukuna",
            originalTitle = "Jujutsu Kaisen: Shibuya Incident",
            type = VideoType.ANIME,
            seasonEpisode = "Season 2",
            year = 2023,
            genres = listOf("Action", "Supernatural", "Dark Fantasy", "Shounen"),
            durationSeconds = 1520, // 25m 20s
            thumbnailUrl = "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=600",
            creatorId = "user_creator",
            creatorName = "Vannak Recap KH",
            creatorAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
            viewsCount = 241000,
            likesCount = 21500,
            dislikesCount = 180,
            isApproved = true,
            isFeatured = true,
            synopsis = "ការសម្រាយព្រឹត្តិការណ៍ដ៏បង្ហូរឈាមបំផុតក្នុងប្រវត្តិសាស្ត្រ Jujutsu Kaisen! ផែនការបិទជិត Gojo Satoru នៅក្នុង Prison Realm និងការបំផ្លិចបំផ្លាញទីក្រុង Shibuya ដោយស្តេចបណ្តាសា Sukuna។",
            chapters = listOf(
                VideoChapter(0, "ផែនការ Halloween នៅស្ថានីយ៍ Shibuya", "ការដាក់រនាំង Veil និងអន្ទាក់របស់ Kenjaku"),
                VideoChapter(310, "Gojo Satoru 0.2s Domain Expansion", "ការលះបង់ និងការបិទជិតក្នុង Prison Realm"),
                VideoChapter(720, "Toji Fushiguro វិលត្រឡប់", "ការលុកលុយរបស់បិសាចដែលគ្មានថាមពលបណ្តាសា"),
                VideoChapter(1040, "Sukuna vs Mahoraga", "មហាវិនាសកម្ម Malevolent Shrine"),
                VideoChapter(1380, "Yuji Itadori vs Mahito", "I am you: ការប្រយុទ្ធចុងក្រោយ")
            ),
            ranks = listOf(
                RankHierarchy(
                    entityName = "Gojo Satoru",
                    category = "Jujutsu Sorcerer Grade",
                    rankSystem = "Grade 4 / 3 / 2 / 1 / Special Grade",
                    currentRank = "Special Grade (The Strongest)",
                    significance = "តុល្យភាពនៃពិភពបណ្តាសាទាំងមូលពឹងផ្អែកលើវត្តមានរបស់គាត់តែម្នាក់"
                ),
                RankHierarchy(
                    entityName = "Ryomen Sukuna",
                    category = "Cursed Spirit / King of Curses",
                    rankSystem = "Special Grade Cursed Object (20 Fingers)",
                    currentRank = "15 Fingers Power unleashed in Shibuya",
                    significance = "កម្លាំងបំផ្លិចបំផ្លាញកម្រិតមហន្តរាយទីក្រុង"
                )
            ),
            factions = listOf(
                FactionStatus(
                    factionName = "Jujutsu High (Tokyo & Kyoto)",
                    factionType = "Sorcerer Academic Institution",
                    reputation = "Official Protectors",
                    characterRole = "First Year Students & Grade 1 Mentors",
                    storyImpact = "ព្យាយាមជួយសង្គ្រោះប្រជាជន និងរំដោះ Gojo"
                ),
                FactionStatus(
                    factionName = "Kenjaku & Disaster Curses",
                    factionType = "Alliance of Special Grade Curses",
                    reputation = "Terrorist / Calamity",
                    characterRole = "Mastermind Plotters",
                    storyImpact = "គោលបំណងផ្លាស់ប្តូរការវិវត្តន៍របស់មនុស្សជាតិ"
                )
            ),
            khmerScript = KhmerScript(
                recapTitle = "សម្រាយរឿង សង្គ្រាម Shibuya - យប់ដែលផ្លាស់ប្តូរជោគវាសនា Sorcerer",
                shortHook = "នៅពេលដែលមនុស្សដែលខ្លាំងបំផុតក្នុងលោកត្រូវបានគេបិទជិត ពិភពលោកទាំងមូលបានធ្លាក់ចូលទៅក្នុងភាពវឹកវរ និងភាពអស់សង្ឃឹមយ៉ាងជ្រាលជ្រៅ!",
                introPhrase = "នៅដើមសាច់រឿង គេបានបង្ហាញឲ្យឃើញថា នៅរាត្រីបុណ្យ Halloween ថ្ងៃទី ៣១ ខែតុលា ទីក្រុង Shibuya ត្រូវបានគ្របដណ្តប់ដោយរនាំងវេទមន្តដ៏ធំមួយ ដែលឃុំឃាំងមនុស្សរាប់ម៉ឺននាក់នៅខាងក្នុង។",
                worldExplanation = "នៅក្នុងពិភព Jujutsu អ្នកប្រើប្រាស់ថាមពលបណ្តាសាត្រូវបានបែងចែកពី Grade 4 ដល់ Special Grade។ Gojo Satoru គឺជា Special Grade ដែលគ្មាននរណាអាចប៉ះពាល់បានដោយសារ Limitless និង Six Eyes។",
                characterIntro = "ដើម្បីប្រឈមមុខនឹងស្ថានការណ៍នេះ ក្រុមរបស់ Yuji Itadori, Megumi, និង Nobara រួមជាមួយគ្រូ Grade 1 ដូចជា Nanami ត្រូវបានបញ្ជូនចូលទៅក្នុងសមរភូមិ។",
                mainStoryRecap = "Kenjaku បានដឹងថា គ្មាននរណាអាចសម្លាប់ Gojo បានទេ ដូច្នេះគាត់បានរៀបចំផែនការប្រើប្រាស់មនុស្សស្លូតត្រង់ធ្វើជាខែលការពារ។ Gojo បានសម្រេចចិត្តបើក Domain Expansion រយៈពេលត្រឹមតែ 0.2 វិនាទី ដើម្បីកម្ចាត់សត្រូវដោយមិនសម្លាប់មនុស្ស។ ប៉ុន្តែភ្លាមៗនោះ Prison Realm ត្រូវបានបើកឡើង ហើយបានបិទជិត Gojo បានជោគជ័យ!",
                rankSystemBreakdown = "ការបាត់បង់ Gojo ដែលជា Special Grade កំពូល បានធ្វើឲ្យកម្រិតគ្រោះថ្នាក់នៃទីក្រុង Shibuya ឡើងដល់ចំណុចក្រហមភ្លាមៗ។",
                factionDynamics = "ក្រុម Cursed Spirits បានចាប់ផ្តើមវាយប្រហារសម្លាប់ Sorcerer ទាំងអស់ ខណៈពេលដែល Yuji ត្រូវបានបង្ខំឲ្យលេបម្រាមដៃរបស់ Sukuna បន្ថែមរហូតដល់ 15 ម្រាមដៃ។",
                majorBattleClimax = "Sukuna បានភ្ញាក់ឡើង ហើយបានប្រយុទ្ធជាមួយ Mahoraga ដែលជាមេទ័ពបិសាចដែល Megumi កោះហៅមក។ Sukuna បានប្រើ Domain 'Malevolent Shrine' បំផ្លាញរាល់វត្ថុក្នុងរង្វង់ 140 ម៉ែត្រ ធ្វើឲ្យ Shibuya ក្លាយជាផេះផង់!",
                endingDirection = "Yuji ត្រូវភ្ញាក់ឡើងប្រឈមមុខនឹងការពិតដ៏ឃោរឃៅដែល Sukuna បានធ្វើ។ Shibuya ត្រូវបានបំផ្លាញ Gojo ត្រូវបានបិទជិត ហើយសមរភូមិ Culling Game កំពុងរង់ចាំនៅខាងមុខ!",
                visualActionNotes = "រូបភាព Yuji លុតជង្គង់កណ្តាលទីក្រុងដែលឆេះខ្ទេចខ្ទី ទឹកភ្នែកហូរស្រែកទ្រហោយំដោយសារវិប្បដិសារី។"
            )
        ),
        RecapVideo(
            id = "recap_frieren",
            title = "សម្រាយរឿង Frieren: Beyond Journey's End - ដំណើរស្វែងយល់ពីបេះដូងមនុស្សរបស់ Elf អាយុរាប់ពាន់ឆ្នាំ",
            originalTitle = "Sousou no Frieren (葬送のフリーレン)",
            type = VideoType.ANIME,
            seasonEpisode = "Season 1 (Episodes 1 - 28)",
            year = 2024,
            genres = listOf("Fantasy", "Adventure", "Drama", "Magic"),
            durationSeconds = 1380, // 23m
            thumbnailUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600",
            creatorId = "user_creator",
            creatorName = "Vannak Recap KH",
            creatorAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
            viewsCount = 98000,
            likesCount = 11200,
            dislikesCount = 45,
            isApproved = true,
            isFeatured = false,
            synopsis = "សម្រាយរឿងបែបអារម្មណ៍ជ្រាលជ្រៅ និងទស្សនវិជ្ជាជីវិត! បន្ទាប់ពីកម្ចាត់ Demon King ក្រុមវីរបុរសបានបែកគ្នា។ Elf ឈ្មោះ Frieren ដែលមានអាយុវែងត្រូវរៀនយល់ពីតម្លៃនៃពេលវេលា និងមនុស្សបន្ទាប់ពីមិត្តភក្តិរបស់នាងបានលាចាកលោក។",
            chapters = listOf(
                VideoChapter(0, "ការវិលត្រឡប់ក្រោយជ័យជម្នះ 10 ឆ្នាំ", "ការបែកគ្នានៃក្រុម Hero"),
                VideoChapter(240, "ពិធីបុណ្យសពរបស់ Himmel", "ការយំសោក និងការភ្ញាក់រលឹក"),
                VideoChapter(560, "ការជួបជាមួយ Fern និង Stark", "ការបន្តវេនជំនាន់ថ្មី"),
                VideoChapter(920, "Aura the Guillotine", "Frieren the Slayer និងជញ្ជីង Mana"),
                VideoChapter(1200, "First Class Mage Exam", "ដំណើរឆ្ពោះទៅកាន់ Ende (Heaven)")
            ),
            ranks = listOf(
                RankHierarchy(
                    entityName = "Frieren",
                    category = "Mage Qualification",
                    rankSystem = "Holy Emblem / First Class Mage",
                    currentRank = "First Class Mage Candidate / Ancient Elf Mage",
                    significance = "អាចលាក់បាំងបរិមាណ Mana របស់ខ្លួនបានរាប់ពាន់ឆ្នាំ ដើម្បីបញ្ឆោត Demon"
                )
            ),
            factions = listOf(
                FactionStatus(
                    factionName = "Hero Party (ក្រុមវីរបុរស Himmel)",
                    factionType = "Adventurer Party",
                    reputation = "Legendary Saviors of the World",
                    characterRole = "Party Mage",
                    storyImpact = "បានសង្គ្រោះពិភពលោក និងបានបន្សល់ទុកអនុស្សាវរីយ៍ដ៏មានតម្លៃ"
                )
            ),
            khmerScript = KhmerScript(
                recapTitle = "សម្រាយរឿង Frieren - ដំណើរក្រោយពេលពិភពលោកបានសន្តិភាព",
                shortHook = "សម្រាប់ Elf អាយុ 10 ឆ្នាំប្រៀបដូចជាពព្រិចភ្នែក ប៉ុន្តែសម្រាប់មនុស្សធម្មតា វាអាចជាពេលវេលាពាក់កណ្តាលជីវិត!",
                introPhrase = "នៅដើមសាច់រឿង គេបានបង្ហាញឲ្យឃើញថា ពិភពលោកបានបញ្ចប់សង្គ្រាមប្រឆាំងនឹង Demon King រយៈពេល 10 ឆ្នាំ។ ក្រុមវីរបុរសទាំង 4 បានវិលត្រឡប់មកវិញដោយជោគជ័យ។",
                worldExplanation = "ពិភពវេទមន្តនេះដំណើរការដោយ Mana និងការស្រមើស្រមៃ។ Demon គឺជាសត្វដែលប្រើភាសាមនុស្សដើម្បីតែបោកបញ្ឆោត ហើយវាស់វែងឋានៈតាមបរិមាណ Mana។",
                characterIntro = "Frieren គឺជា Mage ពូជ Elf ដែលរស់នៅជាង 1000 ឆ្នាំមកហើយ។ នាងមិនធ្លាប់យល់ពីអារម្មណ៍ និងការចងចាំរបស់មនុស្សឡើយ រហូតដល់ Himmel the Hero បានស្លាប់ដោយសារជរាពាធ។",
                mainStoryRecap = "បន្ទាប់ពី Himmel បានចែកឋាន Frieren បានយំហើយមានវិប្បដិសារីថានាងមិនដែលចំណាយពេលស្វែងយល់ពីគាត់ឡើយ។ ហេតុនេះហើយ នាងបានសម្រេចចិត្តចេញដំណើរម្តងទៀតឆ្ពោះទៅកាន់ភាគខាងជើង ដើម្បីជួបវិញ្ញាណ Himmel ម្តងទៀតនៅកន្លែងដែលគេហៅថា Ende។ ក្នុងដំណើរនោះ នាងបានទទួលសិស្សថ្មីឈ្មោះ Fern និងអ្នកចម្បាំងវ័យក្មេងឈ្មោះ Stark។",
                rankSystemBreakdown = "នៅក្នុងការប្រយុទ្ធជាមួយ Aura the Guillotine ដែលជា Demon កំពូលមានជញ្ជីងថ្លឹង Mana Frieren បានបង្ហាញថានាងបានលាក់ Mana របស់នាងជាង 90% ពេញមួយជីវិត!",
                factionDynamics = "Continental Magic Association បានរៀបចំការប្រឡង First Class Mage ដើម្បីអនុញ្ញាតឲ្យធ្វើដំណើរទៅកាន់ដែនដីភាគខាងជើងដែលគ្រោះថ្នាក់។",
                majorBattleClimax = "ការប្រឈមមុខជាមួយ Aura: Aura គិតថា Mana របស់ខ្លួនខ្លាំងជាង Frieren ហើយបានបញ្ជាឲ្យ Frieren លុតជង្គង់។ ប៉ុន្តែនៅពេល Frieren ដោះលែង Mana ពិតប្រាកដចេញមក Aura បានភ័យស្លន់ស្លោ។ Frieren បានបញ្ជាដោយត្រជាក់ថា 'Aura សម្លាប់ខ្លួនឯងទៅ'!",
                endingDirection = "Frieren និងកូនក្រុមរបស់នាងបានប្រឡងជាប់ និងបន្តដំណើរឆ្ពោះទៅកាន់ពិភពខាងជើង ដើម្បីស្វែងរកចម្លើយនៃបេះដូងមនុស្ស។",
                visualActionNotes = "Aura កាន់ដាវកាត់ក្បាលខ្លួនឯងដោយការបង្ខំពីអំណាច Mana ដ៏មហិមារបស់ Frieren ដែលគ្របដណ្តប់ពេញមេឃ។"
            )
        ),
        RecapVideo(
            id = "recap_oppenheimer",
            title = "សម្រាយរឿង Oppenheimer - ពីអ្នករូបវិទ្យាទ្រឹស្តី ដល់បិតាគ្រាប់បែកបរមាណូដែលផ្លាស់ប្តូរជោគវាសនាមនុស្សជាតិ",
            originalTitle = "Oppenheimer (Christopher Nolan)",
            type = VideoType.MOVIE,
            seasonEpisode = "Movie (Feature Film)",
            year = 2023,
            genres = listOf("Biography", "Drama", "History", "War"),
            durationSeconds = 1680, // 28m
            thumbnailUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=600",
            creatorId = "user_creator",
            creatorName = "Vannak Recap KH",
            creatorAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
            viewsCount = 112000,
            likesCount = 8900,
            dislikesCount = 95,
            isApproved = true,
            isFeatured = false,
            synopsis = "សម្រាយសាច់រឿងខ្សែភាពយន្តខ្នាតធំរបស់ Christopher Nolan! ជីវិតរបស់ J. Robert Oppenheimer ក្នុងការដឹកនាំគម្រោង Manhattan Project បង្កើតគ្រាប់បែកបរមាណូ និងវិប្បដិសារីនយោបាយក្រោយសង្គ្រាមលោកលើកទី២។",
            chapters = listOf(
                VideoChapter(0, "ទ្រឹស្តី Quantum Mechanics នៅ Cambridge", "ការចាប់ផ្តើមនៃការស្រាវជ្រាវរូបវិទ្យា"),
                VideoChapter(360, "The Manhattan Project & Los Alamos", "ការប្រមូលផ្តុំអ្នកវិទ្យាសាស្ត្រកំពូល"),
                VideoChapter(840, "Trinity Test: ការបំផ្ទុះដំបូងក្នុងប្រវត្តិសាស្ត្រ", "Now I am become Death"),
                VideoChapter(1220, "ការសវនាការសម្ងាត់ Security Clearance", "Lewis Strauss និងការសងសឹកនយោបាយ"),
                VideoChapter(1520, "កិច្ចសន្ទនាជាមួយ Einstein", "The Chain Reaction of the World")
            ),
            ranks = listOf(
                RankHierarchy(
                    entityName = "J. Robert Oppenheimer",
                    category = "Scientific Leadership",
                    rankSystem = "Director of Los Alamos Laboratory",
                    currentRank = "Chief Scientific Director",
                    significance = "អ្នកសម្របសម្រួលបញ្ញាវន្តរូបវិទ្យាកំពូលៗរបស់ពិភពលោកដើម្បីប្រណាំងប្រជែងជាមួយណាស៊ី"
                )
            ),
            factions = listOf(
                FactionStatus(
                    factionName = "Manhattan Project / US Military",
                    factionType = "Classified War Program",
                    reputation = "Top Secret Priority 1",
                    characterRole = "Civilian Director",
                    storyImpact = "បានផលិតគ្រាប់បែកបរមាណូដំបូងបង្អស់"
                ),
                FactionStatus(
                    factionName = "Atomic Energy Commission (AEC)",
                    factionType = "Government Commission",
                    reputation = "Cold War Oversight",
                    characterRole = "Advisor under suspicion",
                    storyImpact = "បានដកហូត Security Clearance របស់ Oppenheimer"
                )
            ),
            khmerScript = KhmerScript(
                recapTitle = "សម្រាយរឿង Oppenheimer - ភ្លើងព្រះ Prometheus នៃសម័យទំនើប",
                shortHook = "បុរសម្នាក់បានផ្តល់អំណាចដល់មនុស្សជាតិដែលអាចបំផ្លាញពិភពលោកទាំងមូល ហើយបន្ទាប់មកត្រូវពិភពលោកកាត់ទោសដោយសារវិប្បដិសារីរបស់គាត់!",
                introPhrase = "នៅដើមសាច់រឿង គេបានបង្ហាញឲ្យឃើញថា ពិភពលោកក្នុងកំឡុងសង្គ្រាមលោកលើកទី២ កំពុងប្រឈមនឹងការប្រណាំងប្រជែងដើម្បីផលិតអាវុធប្រល័យលោកមុនពេលអាល្លឺម៉ង់ណាស៊ីធ្វើវាបាន។",
                worldExplanation = "រឿងនេះបង្ហាញពីពិភពរូបវិទ្យានុយក្លេអ៊ែរដែលការបំបែកអាតូមអាចបញ្ចេញថាមពលមហិមាដែលមិនធ្លាប់មានពីមុនមក។",
                characterIntro = "J. Robert Oppenheimer គឺជាអ្នករូបវិទ្យាទ្រឹស្តីជនជាតិអាមេរិកដែលមានទេពកោសល្យខ្ពស់ ប៉ុន្តែមានភាពស្មុគស្មាញក្នុងផ្លូវចិត្ត។",
                mainStoryRecap = "ឧត្តមសេនីយ៍ Leslie Groves បានជ្រើសរើស Oppenheimer ឲ្យដឹកនាំគម្រោងសម្ងាត់ Manhattan Project នៅតំបន់ដាច់ស្រយាល Los Alamos។ ពួកគេបានប្រមូលអ្នកវិទ្យាសាស្ត្រពូកែៗមកសាងសង់ទីក្រុងមួយដើម្បីផលិតគ្រាប់បែក។",
                rankSystemBreakdown = "ទោះបីជា Oppenheimer មិនមែនជាទាហានក៏ដោយ គាត់មានឥទ្ធិពលលើសពីឧត្តមសេនីយ៍ដោយសារចំណេះដឹងវិទ្យាសាស្ត្ររបស់គាត់។",
                factionDynamics = "រវាងយោធាដែលចង់បានអាវុធភ្លាមៗ និងអ្នកវិទ្យាសាស្ត្រដែលបារម្ភពីសីលធម៌ Oppenheimer ត្រូវធ្វើជាស្ពានចរចាដ៏លំបាក។",
                majorBattleClimax = "ការធ្វើតេស្ត Trinity Test នៅវាលខ្សាច់ New Mexico: ភាពស្ងប់ស្ងាត់ដ៏គួរឲ្យភ័យខ្លាច មុនពេលពន្លឺដ៏ភ្លឺជាងព្រះអាទិត្យផ្ទុះឡើង និងរលកកម្តៅគ្របដណ្តប់។ Oppenheimer បានរំលឹកពាក្យក្នុងគម្ពីរហិណ្ឌូថា 'ឥឡូវនេះ ខ្ញុំបានក្លាយជាមច្ចុរាជ ជាអ្នកបំផ្លាញពិភពលោក'។",
                endingDirection = "ក្រោយសង្គ្រាម Oppenheimer បានប្រឆាំងនឹងគ្រាប់បែកអ៊ីដ្រូសែន ដែលធ្វើឲ្យគាត់ត្រូវគេចោទប្រកាន់ជាចារកម្មកុម្មុយនិស្ត។ គាត់បានបាត់បង់សិទ្ធិអំណាច ប៉ុន្តែប្រវត្តិសាស្ត្របានចារិកឈ្មោះគាត់ជាបិតាបរមាណូដែលបានផ្លាស់ប្តូរពិភពលោកជារៀងរហូត។",
                visualActionNotes = "ការសន្ទនាស្ងាត់ៗរវាង Oppenheimer និង Albert Einstein ក្បែរបឹង ដោយមានតំណក់ទឹកភ្លៀងធ្លាក់បង្កើតជារលកតូចៗ ដែលជានិមិត្តរូបនៃប្រតិកម្មខ្សែសង្វាក់បំផ្លាញពិភពលោក។"
            )
        ),
        RecapVideo(
            id = "recap_pending_demon_slayer",
            title = "សម្រាយរឿង Demon Slayer - Hashira Training Arc ការហ្វឹកហាត់កម្រិតកំពូលមុនសង្គ្រាមប្រាសាទ Infinity Castle",
            originalTitle = "Kimetsu no Yaiba: Hashira Geiko-hen",
            type = VideoType.ANIME,
            seasonEpisode = "Season 4",
            year = 2024,
            genres = listOf("Action", "Fantasy", "Historical", "Supernatural"),
            durationSeconds = 1150,
            thumbnailUrl = "https://images.unsplash.com/photo-1563089145-599997674d42?w=600",
            creatorId = "user_creator",
            creatorName = "Vannak Recap KH",
            creatorAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
            viewsCount = 0,
            likesCount = 0,
            dislikesCount = 0,
            isApproved = false, // PENDING ADMIN APPROVAL
            approvalNotes = "កំពុងរង់ចាំ Admin ពិនិត្យគុណភាពសម្រាយ និងសិទ្ធិប្រើប្រាស់រូបភាព",
            submittedDate = "2024-10-08",
            synopsis = "សេចក្តីព្រាងសម្រាយរឿងរដូវកាល Hashira Training! ពិនិត្យមើលកម្រិត Rank របស់ Demon Slayer Corps និងការដាស់សញ្ញា Demon Slayer Mark នៅលើខ្លួនរបស់សសរស្តម្ភ Hashira ម្នាក់ៗ។",
            chapters = listOf(
                VideoChapter(0, "ការប្រជុំបន្ទាន់របស់ Hashira", "ការស្វែងយល់ពីលក្ខខណ្ឌ Mark"),
                VideoChapter(300, "ការហ្វឹកហាត់ជាមួយ Tengen Uzui & Muichiro", "ការស៊ូទ្រាំ និងល្បឿនដាវ"),
                VideoChapter(700, "Gyomei Himejima: សសរស្តម្ភថ្មដា", "ការរុញផ្ទាំងថ្មយក្ស"),
                VideoChapter(980, "Muzan មកដល់ផ្ទះ Ubuyashiki", "ការផ្ទុះ និងការបើក Infinity Castle")
            ),
            ranks = listOf(
                RankHierarchy(
                    entityName = "Hashira (Pillars)",
                    category = "Demon Slayer Corps Rank",
                    rankSystem = "Mizunoto to Kinoe -> Hashira",
                    currentRank = "Hashira (Highest Combat Rank)",
                    significance = "អ្នកចម្បាំងកំពូលទាំង 9 ដែលមានសមត្ថភាពប្រឈមមុខនឹង Upper Moon Demons"
                )
            ),
            factions = listOf(
                FactionStatus(
                    factionName = "Demon Slayer Corps",
                    factionType = "Underground Organization",
                    reputation = "Unrecognized by Government",
                    characterRole = "Corps Swordsmen",
                    storyImpact = "ប្តេជ្ញាកម្ចាត់ Muzan Kibutsuji អស់រយៈពេលជាង 1000 ឆ្នាំ"
                )
            ),
            khmerScript = KhmerScript(
                recapTitle = "សម្រាយរឿង Demon Slayer - ការហ្វឹកហាត់ចុងក្រោយរបស់សសរស្តម្ភ",
                shortHook = "ដើម្បីត្រៀមខ្លួនសម្រាប់សង្គ្រាមចុងក្រោយ សូម្បីតែកំពូលអ្នកដាវ Hashira ក៏ត្រូវរងការហ្វឹកហាត់រហូតដល់សន្លប់ជារៀងរាល់ថ្ងៃដែរ!",
                introPhrase = "នៅដើមសាច់រឿង គេបានបង្ហាញឲ្យឃើញថា បន្ទាប់ពី Nezuko អាចទប់ទល់នឹងពន្លឺព្រះអាទិត្យបាន Muzan បានប្តូរទិសដៅដើម្បីចាប់យកនាងជាដាច់ខាត។",
                worldExplanation = "នៅក្នុងអង្គភាព Demon Slayer កម្រិតខ្ពស់បំផុតគឺ Hashira ដែលត្រូវឆ្លងកាត់ការសម្លាប់បិសាច 50 ក្បាល ឬ Lower Moon ម្នាក់។",
                characterIntro = "Tanjiro និងមិត្តភក្តិត្រូវចូលរួមការហ្វឹកហាត់ពិសេសជាមួយ Hashira ទាំងអស់ ដើម្បីដាស់សញ្ញា Demon Slayer Mark ដែលបង្កើនកម្លាំងកាយសម្បទាលើសធម្មជាតិ។",
                mainStoryRecap = "ការហ្វឹកហាត់បានបែងចែកជាដំណាក់កាលពីកាយសម្បទា ល្បឿន ការប្រើដាវ ភាពបត់បែន រហូតដល់កម្លាំងសាច់ដុំជាមួយ Gyomei។",
                rankSystemBreakdown = "ដើម្បីដាស់ Mark បាន ចង្វាក់បេះដូងត្រូវលើសពី 200 bpm និងសីតុណ្ហភាពខ្លួនត្រូវឡើងដល់ 39 អង្សាសេ។",
                factionDynamics = "សមាជិក Corps ទាំងអស់បានរួបរួមគ្នាជាធ្លុងមួយដោយមិនគិតពីឋានៈដើម្បីត្រៀមប្រយុទ្ធ។",
                majorBattleClimax = "Muzan បានលួចចូលមកក្នុងផ្ទះរបស់មេដឹកនាំ Ubuyashiki ប៉ុន្តែ Ubuyashiki បានបំផ្ទុះផ្ទះខ្លួនឯងដើម្បីធ្វើឲ្យ Muzan រងរបួស មុនពេល Hashira ទាំងអស់មកដល់!",
                endingDirection = "ទ្វារ Infinity Castle បានបើកឡើងលេបយក Tanjiro និង Hashira ទាំងអស់ចូលទៅក្នុងសមរភូមិចុងក្រោយ!",
                visualActionNotes = "Hashira ទាំងអស់ធ្លាក់ចុះក្នុងបន្ទប់វិលវល់នៃ Infinity Castle ខណៈ Muzan សើចចំអកពីខាងលើ។"
            )
        )
    )

    val sampleComments = mutableListOf(
        Comment(
            id = "c1",
            videoId = "recap_solo_leveling",
            userId = "u_fan1",
            userName = "Dara Anime Fan",
            userAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
            role = UserRole.VIEWER,
            content = "សម្រាយបានល្អណាស់បង! ការពន្យល់ពី Rank E ទៅ Shadow Monarch ច្បាស់ៗល្អ មិនច្រឡំ Rank តួអង្គជាមួយ Guild ទេ។ ពេញចិត្ត 10/10!",
            timestampFormatted = "2 ម៉ោងមុន",
            likesCount = 48,
            isLikedByMe = true
        ),
        Comment(
            id = "c2",
            videoId = "recap_solo_leveling",
            userId = "u_fan2",
            userName = "Bopha K-Drama & Anime",
            userAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150",
            role = UserRole.VIEWER,
            content = "ឈុត Igris ប្រយុទ្ធជាមួយ Jin-woo ស្វិតស្វាញមែនទែន! ចាំមើល Season 2 បន្តទៀត។",
            timestampFormatted = "5 ម៉ោងមុន",
            likesCount = 19,
            isLikedByMe = false
        ),
        Comment(
            id = "c3",
            videoId = "recap_jjk_shibuya",
            userId = "u_fan3",
            userName = "Piseth Gamer",
            userAvatar = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=150",
            role = UserRole.VIEWER,
            content = "សោកស្តាយ Nanami និង Gojo ណាស់បង... ការសម្រាយបែប Visual Action នេះធ្វើឲ្យស្រមៃឃើញសកម្មភាពច្បាស់ណាស់!",
            timestampFormatted = "1 ថ្ងៃមុន",
            likesCount = 82,
            isLikedByMe = false
        )
    )
}
