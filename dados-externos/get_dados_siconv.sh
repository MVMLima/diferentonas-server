#!/usr/bin/env bash
# Baixa os dados que usamos de convênios

mkdir -p siconv
cd siconv
curl -o 01_ConveniosProgramas.csv  'https://portal.convenios.gov.br/downloads/01_ConveniosProgramas.csv' -H 'Accept-Encoding: gzip, deflate, sdch' -H 'Accept-Language: en-US,en;q=0.8,pt;q=0.6' -H 'Upgrade-Insecure-Requests: 1' -H 'User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.94 Safari/537.36' -H 'Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8' -H 'Referer: https://portal.convenios.gov.br/informacoes-gerenciais/download-de-dados' -H 'Cookie: COOKIE_SUPPORT=true; JSESSIONID=KGtQBkXfGMYLifE2Cy1SZ36I.2; GUEST_LANGUAGE_ID=pt_BR' -H 'Connection: keep-alive' --compressed --insecure
cd -