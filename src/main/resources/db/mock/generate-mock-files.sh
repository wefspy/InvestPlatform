#!/bin/bash
# =============================================================
# Script to generate mock PDF files and upload them to MinIO
# Usage: ./generate-mock-files.sh
# Requires: MinIO client (mc) configured with alias "local"
# =============================================================

set -e

BUCKET="investplatform"
MC_ALIAS="local"

# Configure MinIO client (adjust URL/credentials as needed)
# mc alias set local http://localhost:9000 minioadmin minioadmin

echo "=== Generating mock PDF files ==="

generate_pdf() {
    local path="$1"
    local title="$2"
    local content="$3"

    mkdir -p "$(dirname "$path")"

    # Generate a minimal valid PDF
    cat > "$path" << PDFEOF
%PDF-1.4
1 0 obj
<< /Type /Catalog /Pages 2 0 R >>
endobj

2 0 obj
<< /Type /Pages /Kids [3 0 R] /Count 1 >>
endobj

3 0 obj
<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792]
   /Contents 4 0 R /Resources << /Font << /F1 5 0 R >> >> >>
endobj

4 0 obj
<< /Length 200 >>
stream
BT
/F1 18 Tf
50 700 Td
(${title}) Tj
0 -30 Td
/F1 12 Tf
(${content}) Tj
0 -20 Td
(Mock document for testing purposes) Tj
0 -20 Td
(Generated: $(date +%Y-%m-%d)) Tj
ET
endstream
endobj

5 0 obj
<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>
endobj

xref
0 6
0000000000 65535 f
0000000009 00000 n
0000000058 00000 n
0000000115 00000 n
0000000266 00000 n
0000000518 00000 n

trailer
<< /Size 6 /Root 1 0 R >>
startxref
595
%%EOF
PDFEOF
    echo "  Created: $path"
}

TMPDIR=$(mktemp -d)
cd "$TMPDIR"

# === Emitent 200 (ООО ТехноИнвест) documents ===
generate_pdf "emitents/200/financial_report_2024.pdf" \
    "Financial Report 2024" \
    "OOO TechnoInvest - Annual Financial Report"

generate_pdf "emitents/200/audit_conclusion_2024.pdf" \
    "Audit Conclusion 2024" \
    "OOO TechnoInvest - Audit Report"

generate_pdf "emitents/200/charter.pdf" \
    "Charter" \
    "OOO TechnoInvest - Company Charter"

generate_pdf "emitents/200/egrul_extract_2024.pdf" \
    "EGRUL Extract" \
    "OOO TechnoInvest - EGRUL Extract 2024"

# === Emitent 201 (ИП Петров) documents ===
generate_pdf "emitents/201/financial_report_2024.pdf" \
    "Financial Report 2024" \
    "IP Petrov A.N. (EcoPak) - Annual Report"

generate_pdf "emitents/201/egrip_extract_2024.pdf" \
    "EGRIP Extract" \
    "IP Petrov A.N. - EGRIP Extract 2024"

# === Investor 300 (Сидорова) documents ===
generate_pdf "investors/300/passport_main.pdf" \
    "Passport - Main Page" \
    "Sidorova E.A. - Passport Main Page"

generate_pdf "investors/300/passport_registration.pdf" \
    "Passport - Registration" \
    "Sidorova E.A. - Passport Registration Page"

generate_pdf "investors/300/snils.pdf" \
    "SNILS" \
    "Sidorova E.A. - SNILS Certificate"

# === Investor 301 (Волков) documents ===
generate_pdf "investors/301/passport_main.pdf" \
    "Passport - Main Page" \
    "Volkov I.D. - Passport Main Page"

generate_pdf "investors/301/ip_registration.pdf" \
    "IP Registration" \
    "Volkov I.D. - IP Registration Certificate"

# === Investor 302 (АО ИнвестГрупп) documents ===
generate_pdf "investors/302/le_registration.pdf" \
    "LE Registration" \
    "AO InvestGrupp - EGRUL Registration"

generate_pdf "investors/302/charter.pdf" \
    "Charter" \
    "AO InvestGrupp - Company Charter"

generate_pdf "investors/302/executive_decision.pdf" \
    "Executive Decision" \
    "AO InvestGrupp - CEO Appointment Decision"

# === Proposal 1 documents ===
generate_pdf "proposals/1/financial_report.pdf" \
    "Financial Report" \
    "Proposal 1 - TechnoInvest Shares - Financial Report"

generate_pdf "proposals/1/issue_decision.pdf" \
    "Issue Decision" \
    "Proposal 1 - Securities Issue Decision"

generate_pdf "proposals/1/draft_contract.pdf" \
    "Draft Contract" \
    "Proposal 1 - Draft Investment Contract"

generate_pdf "proposals/1/risk_warning.pdf" \
    "Risk Warning" \
    "Proposal 1 - Risk Warning Document"

# === Proposal 3 documents ===
generate_pdf "proposals/3/financial_report.pdf" \
    "Financial Report" \
    "Proposal 3 - EcoPak Shares - Financial Report"

generate_pdf "proposals/3/draft_contract.pdf" \
    "Draft Contract" \
    "Proposal 3 - Draft Investment Contract"

generate_pdf "proposals/3/risk_warning.pdf" \
    "Risk Warning" \
    "Proposal 3 - Risk Warning Document"

echo ""
echo "=== Uploading to MinIO bucket: $BUCKET ==="

# Upload all files preserving directory structure
if command -v mc &> /dev/null; then
    mc cp --recursive emitents/ "$MC_ALIAS/$BUCKET/emitents/"
    mc cp --recursive investors/ "$MC_ALIAS/$BUCKET/investors/"
    mc cp --recursive proposals/ "$MC_ALIAS/$BUCKET/proposals/"
    echo ""
    echo "=== Upload complete! ==="
    echo "Files in bucket:"
    mc ls --recursive "$MC_ALIAS/$BUCKET/"
else
    echo "WARNING: MinIO client (mc) not found."
    echo "Files generated in: $TMPDIR"
    echo ""
    echo "To upload manually, install mc and run:"
    echo "  mc alias set local http://localhost:9000 <access-key> <secret-key>"
    echo "  mc cp --recursive $TMPDIR/emitents/ local/$BUCKET/emitents/"
    echo "  mc cp --recursive $TMPDIR/investors/ local/$BUCKET/investors/"
    echo "  mc cp --recursive $TMPDIR/proposals/ local/$BUCKET/proposals/"
    exit 0
fi

# Cleanup
cd /
rm -rf "$TMPDIR"
echo "Done!"
