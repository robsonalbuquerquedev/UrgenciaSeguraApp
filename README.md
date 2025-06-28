# 🚨 UrgenciaSeguraApp

> **Projeto em desenvolvimento para o TCC de Robson Albuquerque - 6º período de Análise e Desenvolvimento de Sistemas**

## 🧠 Resumo

O atendimento pré-hospitalar desempenha um papel crucial na redução da morbimortalidade em situações de urgência e emergência. No entanto, serviços como o SAMU enfrentam desafios, como o alto índice de trotes telefônicos, que impactam sua eficiência.

Pensando nisso, este projeto propõe o desenvolvimento de um **aplicativo móvel para solicitação de atendimentos emergenciais**, com integração futura a um **sistema web para gestão das solicitações** por parte dos profissionais de saúde.

A abordagem metodológica combina **pesquisas quantitativas e qualitativas**, utilizando **questionários e entrevistas semiestruturadas** para analisar a percepção de usuários e profissionais da saúde quanto à viabilidade da proposta.

---

## 🎯 Objetivos

- Facilitar a solicitação de atendimento de urgência via app
- Reduzir o número de trotes telefônicos
- Ajudar na triagem mais rápida e eficiente dos atendimentos
- Integrar com um sistema web de acompanhamento pelas equipes de saúde

---

## 📱 Tecnologias utilizadas

- **Kotlin** com Android Studio (aplicativo móvel)
- **Firebase Realtime Database** (armazenamento de dados)
- **Firebase Authentication** (login/cadastro de usuários)
- **Firebase Storage** (upload de imagens - versão futura)
- **Google Maps API** (captura de localização)
- **GitHub** (versionamento do código)

---

## 🚀 Fluxo de Navegação do App

1. **Splash Screen** com o logotipo e título "Urgência Segura"
2. **Tela de Boas-vindas** com opções de:
   - [ ] Login
   - [ ] Cadastro
3. **Cadastro de Usuário**
   - Envio de dados para o Firebase Authentication e Realtime Database
   - Redirecionamento automático para a tela de login
4. **Login**
   - Verificação via Firebase Authentication
   - Opção "Esqueceu a senha" para recuperação
   - Redirecionamento para tela principal
5. **Tela Principal**
   - [ ] Botão "Solicitar Urgência"
   - [ ] Botão "Sair"
6. **Tela de Solicitação de Urgência**
   - Envio de dados para o Firebase
   - Captura de localização
   - Opção de tirar uma foto
   - Seleção do tipo de atendimento: SAMU ou Defesa Civil

---

## ✅ Funcionalidades já implementadas

- [x] Tela de Splash inicial
- [x] Tela de boas-vindas com opções de login/cadastro
- [x] Cadastro de usuários com Firebase Authentication
- [x] Login de usuários com Firebase Authentication
- [x] Opção "Esqueceu a senha" na tela de login
- [x] Tela principal com opções de Solicitar Urgência ou Sair
- [x] Tela de solicitação de urgência
- [x] Captura de localização em tempo real
- [x] Opção de tirar foto
- [x] Suporte à solicitação para terceiros
- [x] Validação de campos obrigatórios

---

## 🧪 Em desenvolvimento

- [ ] Tela de histórico de solicitações
- [ ] Verificação de permissões e melhorias de UX

---

## 💡 Futuras melhorias

- Sistema de triagem automatizada com base nos dados enviados
- Notificações em tempo real para socorristas
- Painel web administrativo para gestão das solicitações
- Publicação do app na Google Play Store com nome e ícone personalizados

---

## 📷 Capturas de Tela

![Tela inicial](assets/home.jpg)
---

## 📚 Palavras-chave

**Atendimento pré-hospitalar**, **Aplicativo Móvel**, **Emergência Médica**, **SAMU**

---

## 📦 Download do App

➡️ [Clique aqui para baixar o UrgenciaSegura.apk](https://github.com/robsonalbuquerquedev/UrgenciaSeguraApp/releases/download/v1.05-beta/UrgenciaSegura.apk)

---

## 📲 Como instalar

1. Faça o download do `.apk` acima.
2. No seu celular, vá em **Configurações > Segurança**.
3. Ative a opção **Fontes desconhecidas** ou **Permitir instalação de apps desconhecidos**.
4. Localize o arquivo `UrgenciaSegura.apk` na pasta de downloads.
5. Toque no arquivo e siga os passos para instalar.

> ⚠️ Esta é uma versão de testes. Recomendamos cautela ao utilizá-la.  
> Para dúvidas ou feedback, entre em contato no e-mail do desenvolvedor.

---

## 📎 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

## 🤝 Contribuições

Este é um projeto acadêmico, mas colaborações e feedbacks são bem-vindos!  
Sinta-se à vontade para abrir uma issue ou enviar um pull request.

---

## 📬 Contato

Robson Albuquerque  
E-mail: *robalbuquerque98@gmail.com*  
LinkedIn: *[LinkedIn](https://www.linkedin.com/in/robson-monteiro-de-albuquerque-8b3853230 )*

---

![Status](https://img.shields.io/badge/Status-Em%20Desenvolvimento-yellow)
![License](https://img.shields.io/badge/Licença-MIT-blue)
![Plataforma](https://img.shields.io/badge/Plataforma-Android-green)

---

![Linguagem](https://img.shields.io/github/languages/top/robsonalbuquerquedev/UrgenciaSeguraApp)
![Último commit](https://img.shields.io/github/last-commit/robsonalbuquerquedev/UrgenciaSeguraApp)
![Repo size](https://img.shields.io/github/repo-size/robsonalbuquerquedev/UrgenciaSeguraApp)
