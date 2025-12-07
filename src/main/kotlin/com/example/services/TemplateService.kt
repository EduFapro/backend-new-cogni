package com.example.services

data class ModuleTemplate(val id: Int, val title: String)
data class TaskTemplate(val id: Int, val moduleId: Int, val title: String)

class TemplateService {
    
    val modules = listOf(
        ModuleTemplate(1, "Dados Pessoais"),
        ModuleTemplate(2, "Funções Cognitivas"),
        ModuleTemplate(3, "Funcionalidade"),
        ModuleTemplate(4, "Sintomas de Depressão"),
        ModuleTemplate(9001, "Teste de Áudio e Gravação")
    )

    val tasks = listOf(
        // Sociodemographic (Module 1)
        TaskTemplate(1, 1, "Seja bem-vindo"),
        TaskTemplate(2, 1, "Dados pessoais - nome"),
        TaskTemplate(3, 1, "Dados pessoais - nascimento"),
        TaskTemplate(4, 1, "Escolaridade"),
        TaskTemplate(5, 1, "Profissão"),
        TaskTemplate(6, 1, "Com quem mora"),
        TaskTemplate(7, 1, "Exercícios físicos"),
        TaskTemplate(8, 1, "Leitura"),
        TaskTemplate(9, 1, "Jogos e passatempos"),
        TaskTemplate(10, 1, "Doenças diagnosticadas"),

        // Cognitive Functions (Module 2)
        TaskTemplate(11, 2, "Preste bastante atenção"),
        TaskTemplate(12, 2, "Cálculo seriado"),
        TaskTemplate(13, 2, "Em que ano estamos?"),
        TaskTemplate(14, 2, "Em que mês estamos?"),
        TaskTemplate(15, 2, "Que dia do mês é hoje?"),
        TaskTemplate(16, 2, "Que dia da semana é hoje?"),
        TaskTemplate(17, 2, "Qual a sua idade?"),
        TaskTemplate(18, 2, "Onde estamos agora?"),
        TaskTemplate(19, 2, "Quem é o atual presidente do Brasil?"),
        TaskTemplate(20, 2, "Quem foi o presidente anterior?"),
        TaskTemplate(21, 2, "Repetir palavras (1ª vez)"),
        TaskTemplate(22, 2, "Recordar palavras (1ª vez)"),
        TaskTemplate(23, 2, "Repetir palavras (2ª vez)"),
        TaskTemplate(24, 2, "Recordar palavras (2ª vez)"),
        TaskTemplate(25, 2, "Repetir palavras (3ª vez)"),
        TaskTemplate(26, 2, "Recordar palavras (3ª vez)"),
        TaskTemplate(27, 2, "O que você fez ontem?"),
        TaskTemplate(28, 2, "Brincadeira favorita na infância"),
        TaskTemplate(29, 2, "Repetir palavras ouvidas anteriormente"),
        TaskTemplate(30, 2, "Preste atenção na história"),
        TaskTemplate(31, 2, "História do gato da Ana"),
        TaskTemplate(32, 2, "Quantos animais você consegue dizer?"),
        TaskTemplate(33, 2, "Palavras iniciadas com a letra F"),
        TaskTemplate(34, 2, "Palavras iniciadas com a letra A"),
        TaskTemplate(35, 2, "Palavras iniciadas com a letra S"),
        TaskTemplate(36, 2, "Descreva o que você vê"),
        TaskTemplate(37, 2, "Conte a história novamente"),

        // Functionality (Module 3)
        TaskTemplate(38, 3, "Perguntas de sim ou não"),
        TaskTemplate(39, 3, "Banho sozinho"),
        TaskTemplate(40, 3, "Vestir-se sozinho"),
        TaskTemplate(41, 3, "Usar o banheiro sozinho"),
        TaskTemplate(42, 3, "Usar o telefone"),
        TaskTemplate(43, 3, "Fazer compras"),
        TaskTemplate(44, 3, "Lidar com dinheiro"),
        TaskTemplate(45, 3, "Medicamentos"),
        TaskTemplate(46, 3, "Transporte"),

        // Depression Symptoms (Module 4)
        TaskTemplate(47, 4, "Sentimentos nas últimas duas semanas"),
        TaskTemplate(48, 4, "Triste frequentemente"),
        TaskTemplate(49, 4, "Cansaço / falta de energia"),
        TaskTemplate(50, 4, "Dificuldade para dormir"),
        TaskTemplate(51, 4, "Preferência por ficar em casa"),
        TaskTemplate(52, 4, "Sentir-se inútil ou culpado"),
        TaskTemplate(53, 4, "Perda de interesse em atividades"),
        TaskTemplate(54, 4, "Esperança em relação ao futuro"),
        TaskTemplate(55, 4, "Sentir que a vida vale a pena"),
        TaskTemplate(56, 4, "Agradecimento pela participação"),

        // Tests (Module 9001)
        TaskTemplate(9001, 9001, "A pressa é inimiga da perfeição"),
        TaskTemplate(9002, 9001, "Conte até 5")
    )

    fun getAllModules(): List<ModuleTemplate> = modules

    fun getTasksByModuleId(moduleId: Int): List<TaskTemplate> {
        return tasks.filter { it.moduleId == moduleId }
    }
}
