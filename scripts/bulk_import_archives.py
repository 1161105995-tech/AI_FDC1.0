#!/usr/bin/env python
# -*- coding: utf-8 -*-

from __future__ import annotations

import argparse
import json
import mimetypes
import re
import sys
import time
from dataclasses import dataclass
from datetime import datetime
from pathlib import Path
from typing import Any

import requests


DEFAULT_ROOT = Path("D:/AI project/AI-search/\u6d4b\u8bd5\u6587\u4ef6\u76ee\u5f55")
DEFAULT_API_BASE = "http://localhost:8080/api"
DEFAULT_OUTPUT_DIR = Path("D:/AI project/smart-archive-management-system/import-results")
EXT_VALUE_DEFAULT = "\u7cfb\u7edf\u9a8c\u8bc1\u81ea\u52a8\u5bfc\u5165"

FILE_TYPES = {".pdf", ".doc", ".docx"}
CONTRACT_TYPES = {"DOC_CON_PTP", "\u9500\u552e\u5408\u540c"}
FINANCE_TYPES = {"DOC_FIN_VCH_REC", "AP_INVOICE", "AR_CASH", "DOC_FIN_VCH_PAY", "DOC_FIN_VCH_PAY_DOM"}
COUNTRY_NAME_MAP = {
    "CN": "\u4e2d\u56fd",
    "US": "\u7f8e\u56fd",
    "DE": "\u5fb7\u56fd",
    "CH": "\u745e\u58eb",
}
BUSINESS_CODE_ALIASES = {
    "CT": "CHINA-TELECOM",
    "CU": "CHINA-UNICOM",
}
TYPE_HINTS = [
    ("\u91c7\u8d2d\u5408\u540c", "DOC_CON_PTP"),
    ("\u9500\u552e\u5408\u540c", "\u9500\u552e\u5408\u540c"),
    ("\u5e94\u6536\u53d1\u7968", "DOC_FIN_VCH_REC"),
    ("\u5e94\u4ed8\u53d1\u7968", "AP_INVOICE"),
    ("\u6536\u6b3e\u5355", "AR_CASH"),
    ("\u4ed8\u6b3e\u51ed\u8bc1", "DOC_FIN_VCH_PAY"),
]
BRACKET_PATTERN = re.compile(r"[\(\uff08]([^\)\uff09]+)[\)\uff09]")
DATE_PATTERN = re.compile(r"(20\d{2})(\d{2})(\d{2})")


def normalize_text(value: str | None) -> str:
    if not value:
        return ""
    normalized = value.strip().lower()
    normalized = normalized.replace("\u3000", "").replace(" ", "")
    normalized = normalized.replace("\uff08", "(").replace("\uff09", ")")
    normalized = normalized.replace("\u6709\u9650\u516c\u53f8", "")
    normalized = normalized.replace("\u516c\u53f8", "")
    normalized = normalized.replace("\u96c6\u56e2", "")
    normalized = normalized.replace("(", "").replace(")", "")
    return normalized


def infer_document_type_from_name(file_name: str) -> str | None:
    for label, code in TYPE_HINTS:
        if file_name.startswith(label):
            return code
    return None


def infer_company_name_from_name(file_name: str) -> str | None:
    match = BRACKET_PATTERN.search(file_name)
    return match.group(1).strip() if match else None


def infer_date_from_name(file_name: str) -> str | None:
    match = DATE_PATTERN.search(file_name)
    if not match:
        return None
    return f"{match.group(1)}-{match.group(2)}-{match.group(3)}"


def derive_period(document_date: str | None) -> str | None:
    return document_date[:7] if document_date and len(document_date) >= 7 else None


def choose_archive_type(document_type_code: str) -> str:
    if document_type_code in FINANCE_TYPES:
        return "FINANCIAL"
    if document_type_code in CONTRACT_TYPES:
        return "PROJECT"
    return "GENERAL"


def choose_retention_years(document_type_code: str) -> int:
    if document_type_code in CONTRACT_TYPES:
        return 30
    if document_type_code in FINANCE_TYPES:
        return 10
    return 10


def choose_duty_department(document_type_code: str) -> str:
    if document_type_code == "\u9500\u552e\u5408\u540c":
        return "\u9500\u552e\u90e8\u95e8"
    if document_type_code == "DOC_CON_PTP":
        return "\u91c7\u8d2d\u90e8\u95e8"
    return "\u8d22\u52a1\u90e8\u95e8"


@dataclass
class OptionItem:
    code: str
    name: str


class ArchiveImporter:
    def __init__(self, api_base: str, output_dir: Path, timeout: int, dry_run: bool, limit: int | None) -> None:
        self.api_base = api_base.rstrip("/")
        self.timeout = timeout
        self.dry_run = dry_run
        self.limit = limit
        self.session = requests.Session()
        self.output_dir = output_dir
        self.output_dir.mkdir(parents=True, exist_ok=True)
        timestamp = datetime.now().strftime("%Y%m%d-%H%M%S")
        self.report_path = self.output_dir / f"bulk-import-report-{timestamp}.jsonl"
        self.summary_path = self.output_dir / f"bulk-import-summary-{timestamp}.json"
        self.create_options = self._get_json("/archive-management/create/options")["data"]
        self.company_code_by_name = self._build_company_index(self.create_options["companyProjects"])
        self.document_org_by_country = self._build_org_index(self.create_options["documentOrganizations"])
        self.ext_fields_cache: dict[str, list[dict[str, Any]]] = {}
        self.stats = {
            "processed": 0,
            "created": 0,
            "skipped": 0,
            "failed": 0,
            "startedAt": datetime.now().isoformat(timespec="seconds"),
        }

    def _log(self, row: dict[str, Any]) -> None:
        with self.report_path.open("a", encoding="utf-8") as fp:
            fp.write(json.dumps(row, ensure_ascii=False) + "\n")

    def _get_json(self, path: str) -> dict[str, Any]:
        response = self.session.get(f"{self.api_base}{path}", timeout=self.timeout)
        response.raise_for_status()
        payload = response.json()
        if not payload.get("success", False):
            raise RuntimeError(payload.get("message") or f"GET {path} failed")
        return payload

    def _post_json(self, path: str, payload: dict[str, Any]) -> dict[str, Any]:
        response = self.session.post(f"{self.api_base}{path}", json=payload, timeout=self.timeout)
        response.raise_for_status()
        body = response.json()
        if not body.get("success", False):
            raise RuntimeError(body.get("message") or f"POST {path} failed")
        return body

    def _is_text_like(self, head_bytes: bytes) -> bool:
        if not head_bytes:
            return False
        if head_bytes.startswith(b"%PDF") or head_bytes.startswith(b"PK\x03\x04") or head_bytes.startswith(b"\xd0\xcf\x11\xe0"):
            return False
        try:
            decoded = head_bytes.decode("utf-8", errors="ignore")
        except UnicodeDecodeError:
            return False
        if not decoded:
            return False
        printable = sum(1 for char in decoded if char.isprintable() or char in "\r\n\t")
        return printable / max(len(decoded), 1) > 0.8

    def _resolve_upload_name_and_mime(self, file_path: Path) -> tuple[str, str]:
        head_bytes = file_path.read_bytes()[:64]
        if self._is_text_like(head_bytes):
            return f"{file_path.stem}.txt", "text/plain; charset=utf-8"
        return file_path.name, mimetypes.guess_type(file_path.name)[0] or "application/octet-stream"

    def _upload_file(self, session_code: str, file_path: Path) -> dict[str, Any]:
        upload_name, mime_type = self._resolve_upload_name_and_mime(file_path)
        with file_path.open("rb") as fp:
            response = self.session.post(
                f"{self.api_base}/archive-management/create/sessions/{session_code}/attachments",
                data={"attachmentRole": "ELECTRONIC"},
                files={"file": (upload_name, fp, mime_type)},
                timeout=self.timeout,
            )
        response.raise_for_status()
        body = response.json()
        if not body.get("success", False):
            raise RuntimeError(body.get("message") or "upload failed")
        return body

    def _build_company_index(self, company_projects: list[dict[str, str]]) -> dict[str, OptionItem]:
        index: dict[str, OptionItem] = {}
        for item in company_projects:
            option = OptionItem(code=item["code"], name=item["name"])
            index[normalize_text(item["name"])] = option
            index[normalize_text(item["code"])] = option
        return index

    def _build_org_index(self, organizations: list[dict[str, str]]) -> dict[str, OptionItem]:
        index: dict[str, OptionItem] = {}
        for item in organizations:
            option = OptionItem(code=item["code"], name=item["name"])
            for country_code, country_name in COUNTRY_NAME_MAP.items():
                if country_name in item["name"]:
                    index[country_code] = option
        return index

    def _load_effective_ext_fields(self, document_type_code: str) -> list[dict[str, Any]]:
        if document_type_code not in self.ext_fields_cache:
            payload = self._get_json(f"/base-data/document-types/{requests.utils.quote(document_type_code, safe='')}/ext-fields/effective")
            self.ext_fields_cache[document_type_code] = payload["data"] or []
        return self.ext_fields_cache[document_type_code]

    def _resolve_company_from_business_code(self, business_code: str | None) -> tuple[str | None, str | None]:
        if not business_code:
            return None, None
        tokens = [token for token in re.split(r"[^A-Za-z0-9]+", business_code.upper()) if token]
        for start in range(len(tokens)):
            for end in range(len(tokens), start, -1):
                candidate = "-".join(tokens[start:end])
                alias = BUSINESS_CODE_ALIASES.get(candidate)
                if alias:
                    option = self.company_code_by_name.get(normalize_text(alias))
                    if option:
                        return option.code, option.name
                option = self.company_code_by_name.get(normalize_text(candidate))
                if option:
                    return option.code, option.name
        return None, None

    def _resolve_company_from_labeled_text(self, document_type_code: str | None, text: str | None) -> tuple[str | None, str | None]:
        if not text or not document_type_code:
            return None, None
        labels_by_type = {
            "DOC_FIN_VCH_PAY": ["收款方", "收款单位", "收款人"],
            "DOC_FIN_VCH_PAY_DOM": ["收款方", "收款单位", "收款人"],
            "AR_CASH": ["收款方", "收款单位", "收款人"],
            "DOC_FIN_VCH_REC": ["购买方", "购方", "受票方"],
            "AP_INVOICE": ["购买方", "购方", "受票方"],
        }
        for label in labels_by_type.get(document_type_code, []):
            match = re.search(rf"{re.escape(label)}[:：]\s*([^\s，,。；;]+)", text)
            if not match:
                continue
            candidate = match.group(1).strip()
            option = self.company_code_by_name.get(normalize_text(candidate))
            if option:
                return option.code, option.name
        return None, None

    def _resolve_company_from_text(self, text: str | None) -> tuple[str | None, str | None]:
        if not text:
            return None, None
        matched: dict[str, OptionItem] = {}
        lowered = normalize_text(text)
        for key, option in self.company_code_by_name.items():
            if key and key in lowered:
                matched[option.code] = option
        if len(matched) == 1:
            option = next(iter(matched.values()))
            return option.code, option.name
        return None, None

    def _resolve_company(self, file_name: str, parse_result: dict[str, Any], document_type_code: str | None) -> tuple[str | None, str | None]:
        if parse_result.get("companyProjectCode"):
            return parse_result.get("companyProjectCode"), parse_result.get("companyProjectName") or parse_result.get("companyProjectCode")
        candidate = infer_company_name_from_name(file_name)
        option = self.company_code_by_name.get(normalize_text(candidate))
        if option:
            return option.code, option.name
        from_business_code = self._resolve_company_from_business_code(parse_result.get("businessCode"))
        if from_business_code[0]:
            return from_business_code
        combined_text = " ".join(
            filter(
                None,
                [
                    parse_result.get("aiSummary"),
                    parse_result.get("extractedTextPreview"),
                    parse_result.get("parseExplain"),
                ],
            )
        )
        from_labeled_text = self._resolve_company_from_labeled_text(document_type_code, combined_text)
        if from_labeled_text[0]:
            return from_labeled_text
        from_text = self._resolve_company_from_text(
            combined_text
        )
        if from_text[0]:
            return from_text
        return None, candidate

    def _resolve_document_type(self, file_name: str, parse_result: dict[str, Any]) -> str | None:
        inferred = infer_document_type_from_name(file_name)
        if inferred:
            return inferred
        return parse_result.get("suggestedDocumentTypeCode")

    def _resolve_document_date(self, file_path: Path, parse_result: dict[str, Any]) -> str:
        if parse_result.get("documentDate"):
            return parse_result["documentDate"]
        from_name = infer_date_from_name(file_path.name)
        if from_name:
            return from_name
        created = datetime.fromtimestamp(file_path.stat().st_ctime)
        return created.strftime("%Y-%m-%d")

    def _build_ext_values(self, document_type_code: str, document_name: str) -> dict[str, str]:
        result: dict[str, str] = {}
        for field in self._load_effective_ext_fields(document_type_code):
            if field.get("requiredFlag") == "Y":
                result[field["fieldCode"]] = f"{EXT_VALUE_DEFAULT}-{document_name}"
        return result

    def _create_archive_for_file(self, file_path: Path) -> dict[str, Any]:
        document_name = file_path.stem
        create_session = self._post_json("/archive-management/create/sessions", {"createMode": "AUTO"})
        session_code = create_session["data"]["sessionCode"]
        self._upload_file(session_code, file_path)
        session_payload = self._get_json(f"/archive-management/create/sessions/{session_code}")
        session_data = session_payload["data"]
        parse_result = session_data.get("aiParseResult") or {}
        document_type_code = self._resolve_document_type(file_path.name, parse_result)
        company_project_code, company_project_name = self._resolve_company(file_path.name, parse_result, document_type_code)
        document_date = self._resolve_document_date(file_path, parse_result)
        begin_period = parse_result.get("beginPeriod") or derive_period(document_date)
        end_period = parse_result.get("endPeriod") or derive_period(document_date)

        missing = []
        if not document_type_code:
            missing.append("documentTypeCode")
        if not company_project_code:
            missing.append("companyProjectCode")
        if not begin_period:
            missing.append("beginPeriod")
        if not end_period:
            missing.append("endPeriod")
        if missing:
            return {
                "status": "skipped",
                "reason": "missing required inferred fields",
                "missingFields": missing,
                "sessionCode": session_code,
                "documentName": document_name,
                "parseResult": parse_result,
                "companyProjectName": company_project_name,
            }

        defaults = self._get_json(
            "/archive-management/create/defaults"
            f"?companyProjectCode={requests.utils.quote(company_project_code, safe='')}"
            f"&documentTypeCode={requests.utils.quote(document_type_code, safe='')}"
        )["data"]
        country_code = defaults.get("countryCode")
        document_org = self.document_org_by_country.get(country_code or "")
        if not document_org:
            return {
                "status": "skipped",
                "reason": "document organization not found for country",
                "sessionCode": session_code,
                "documentName": document_name,
                "countryCode": country_code,
                "companyProjectCode": company_project_code,
            }

        create_payload = {
            "sessionCode": session_code,
            "createMode": "AUTO",
            "documentTypeCode": document_type_code,
            "companyProjectCode": company_project_code,
            "beginPeriod": begin_period,
            "endPeriod": end_period,
            "businessCode": document_name,
            "documentName": document_name,
            "dutyPerson": "\u5f20\u4e09",
            "dutyDepartment": choose_duty_department(document_type_code),
            "documentDate": document_date,
            "securityLevelCode": "SECRET",
            "sourceSystem": "AI_TEST_IMPORT",
            "carrierTypeCode": "ELECTRONIC",
            "remark": "\u6279\u91cf\u5bfc\u5165\u81ea\u6d4b\u8bd5\u6587\u4ef6\u76ee\u5f55",
            "aiArchiveSummary": session_data.get("aiSummarySnapshot"),
            "documentOrganizationCode": document_org.code,
            "retentionPeriodYears": choose_retention_years(document_type_code),
            "archiveTypeCode": choose_archive_type(document_type_code),
            "countryCode": country_code,
            "extValues": self._build_ext_values(document_type_code, document_name),
        }

        if self.dry_run:
            return {
                "status": "dry_run",
                "sessionCode": session_code,
                "createPayload": create_payload,
                "parseResult": parse_result,
            }

        created = self._post_json("/archive-management/create/archives", create_payload)["data"]
        return {
            "status": "created",
            "sessionCode": session_code,
            "archiveId": created.get("archiveId"),
            "archiveCode": created.get("archiveCode"),
            "documentTypeCode": document_type_code,
            "companyProjectCode": company_project_code,
            "companyProjectName": company_project_name,
            "documentOrganizationCode": document_org.code,
            "documentDate": document_date,
        }

    def run(self, root: Path) -> int:
        files = sorted([path for path in root.iterdir() if path.is_file() and path.suffix.lower() in FILE_TYPES])
        if self.limit is not None:
            files = files[: self.limit]

        for index, file_path in enumerate(files, start=1):
            self.stats["processed"] += 1
            started = time.time()
            try:
                result = self._create_archive_for_file(file_path)
                elapsed = round(time.time() - started, 2)
                row = {
                    "index": index,
                    "fileName": file_path.name,
                    "filePath": str(file_path),
                    "elapsedSeconds": elapsed,
                    **result,
                }
                self._log(row)
                if result["status"] == "created":
                    self.stats["created"] += 1
                else:
                    self.stats["skipped"] += 1
                print(f"[{index}/{len(files)}] {result['status']}: {file_path.name}", flush=True)
            except Exception as exc:  # noqa: BLE001
                elapsed = round(time.time() - started, 2)
                self.stats["failed"] += 1
                self._log(
                    {
                        "index": index,
                        "fileName": file_path.name,
                        "filePath": str(file_path),
                        "status": "failed",
                        "elapsedSeconds": elapsed,
                        "error": str(exc),
                    }
                )
                print(f"[{index}/{len(files)}] failed: {file_path.name} -> {exc}", flush=True)

        self.stats["finishedAt"] = datetime.now().isoformat(timespec="seconds")
        with self.summary_path.open("w", encoding="utf-8") as fp:
            json.dump(self.stats, fp, ensure_ascii=False, indent=2)
        print(json.dumps(self.stats, ensure_ascii=False, indent=2), flush=True)
        print(f"report={self.report_path}", flush=True)
        print(f"summary={self.summary_path}", flush=True)
        return 0 if self.stats["failed"] == 0 else 1


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Bulk import archive files through the create-session API.")
    parser.add_argument("--root", type=Path, default=DEFAULT_ROOT)
    parser.add_argument("--api-base", default=DEFAULT_API_BASE)
    parser.add_argument("--output-dir", type=Path, default=DEFAULT_OUTPUT_DIR)
    parser.add_argument("--timeout", type=int, default=120)
    parser.add_argument("--limit", type=int)
    parser.add_argument("--skip", type=int, default=0)
    parser.add_argument("--dry-run", action="store_true")
    return parser.parse_args()


def main() -> int:
    args = parse_args()
    importer = ArchiveImporter(
        api_base=args.api_base,
        output_dir=args.output_dir,
        timeout=args.timeout,
        dry_run=args.dry_run,
        limit=args.limit,
    )
    if args.skip:
        original_run = importer.run

        def run_with_skip(root: Path) -> int:
            files = sorted([path for path in root.iterdir() if path.is_file() and path.suffix.lower() in FILE_TYPES])
            files = files[args.skip :]
            if importer.limit is not None:
                files = files[: importer.limit]
            if not files:
                print("No files left after skip.", flush=True)
                return 0
            total = len(files)
            for index, file_path in enumerate(files, start=args.skip + 1):
                importer.stats["processed"] += 1
                started = time.time()
                try:
                    result = importer._create_archive_for_file(file_path)
                    elapsed = round(time.time() - started, 2)
                    row = {
                        "index": index,
                        "fileName": file_path.name,
                        "filePath": str(file_path),
                        "elapsedSeconds": elapsed,
                        **result,
                    }
                    importer._log(row)
                    if result["status"] == "created":
                        importer.stats["created"] += 1
                    else:
                        importer.stats["skipped"] += 1
                    print(f"[{index}/{args.skip + total}] {result['status']}: {file_path.name}", flush=True)
                except Exception as exc:  # noqa: BLE001
                    elapsed = round(time.time() - started, 2)
                    importer.stats["failed"] += 1
                    importer._log(
                        {
                            "index": index,
                            "fileName": file_path.name,
                            "filePath": str(file_path),
                            "status": "failed",
                            "elapsedSeconds": elapsed,
                            "error": str(exc),
                        }
                    )
                    print(f"[{index}/{args.skip + total}] failed: {file_path.name} -> {exc}", flush=True)
            importer.stats["finishedAt"] = datetime.now().isoformat(timespec="seconds")
            with importer.summary_path.open("w", encoding="utf-8") as fp:
                json.dump(importer.stats, fp, ensure_ascii=False, indent=2)
            print(json.dumps(importer.stats, ensure_ascii=False, indent=2), flush=True)
            print(f"report={importer.report_path}", flush=True)
            print(f"summary={importer.summary_path}", flush=True)
            return 0 if importer.stats["failed"] == 0 else 1

        importer.run = run_with_skip  # type: ignore[method-assign]
    return importer.run(args.root)


if __name__ == "__main__":
    raise SystemExit(main())
